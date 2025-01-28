package com.tyordanovv.load_balancer.lb.service;

import com.tyordanovv.load_balancer.lb.model.RequestForwardingDto;
import com.tyordanovv.load_balancer.lb.model.ServerStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LoadBalancerService {
    private static final Logger log = LoggerFactory.getLogger(LoadBalancerService.class);
    private final ServerPool serverPool;
    private final RestTemplate restTemplate;
    private final ExecutorService virtualThreadExecutor;

    public LoadBalancerService(
            ServerPool serverPool,
            RestTemplate restTemplate
    ) {
        this.serverPool = serverPool;
        this.restTemplate = restTemplate;
        this.virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor(); // Creating executor for virtual threads
    }

    public void addServer(String url) {
        try {
            serverPool.addServer(url);
            log.info("Server {} was successfully added to the pool.", url);
        } catch (Exception e) {
            log.error("Server {} could not be added to the pool. {}", url, e.getMessage());
        }
    }

    public void removeServer(String url) {
        serverPool.removeServer(url);
    }

    public List<ServerStats> getAvailableServers() {
        return serverPool.getAllServers();
    }

    public ServerStats selectServer() {
        return getAvailableServers().stream()
                .filter(ServerStats::isHealthy)
                .min(Comparator.comparingDouble(ServerStats::getWorkload))
                .orElseThrow(() -> new RuntimeException("No healthy servers available!"));
    }

    public CompletableFuture<ResponseEntity<String>> forwardRequestAsync(RequestForwardingDto dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpEntity<String> requestEntity = new HttpEntity<>(dto.body(), dto.headers());
                ResponseEntity<String> response = restTemplate.exchange(
                        dto.targetUrl(),
                        dto.method(),
                        requestEntity,
                        String.class
                );
                log.info("Request forwarded to {} with response status: {}", dto.targetUrl(), response.getStatusCode());
                return response;
            } catch (Exception ex) {
                log.error("Failed to forward request to {}: {}", dto.targetUrl(), ex.getMessage());
                throw new RuntimeException("Failed to forward request", ex);
            }
        }, virtualThreadExecutor);
    }
}