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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Load balancer service responsible for:
 * - Selecting the healthiest server with the least workload.
 * - Forwarding HTTP requests asynchronously.
 */
@Component
public class LoadBalancerService {
    private static final Logger log = LoggerFactory.getLogger(LoadBalancerService.class);
    private final ServerPoolManager serverPoolManager;
    private final RestTemplate restTemplate;
    private final ExecutorService virtualThreadExecutor;

    public LoadBalancerService(
            ServerPoolManager serverPoolManager,
            RestTemplate restTemplate
    ) {
        this.serverPoolManager = serverPoolManager;
        this.restTemplate = restTemplate;
        this.virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    /**
     * Selects the healthiest server with the lowest workload.
     *
     * @return Selected server with the least workload {@link ServerStats}.
     * @throws RuntimeException if no healthy servers are available.
     */
    public ServerStats selectServer() {
        return serverPoolManager.getAllServers(true).stream()
                .min(Comparator.comparingDouble(ServerStats::getWorkload))
                .orElseThrow(() -> new RuntimeException("No healthy servers available!"));
    }

    /**
     * Forwards an HTTP request asynchronously to the selected server.
     *
     * @param requestDTO {@link RequestForwardingDto} Data Transfer Object containing request details.
     * @return A CompletableFuture containing the HTTP response.
     */
    public CompletableFuture<ResponseEntity<String>> forwardRequestAsync(RequestForwardingDto requestDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpEntity<String> requestEntity = new HttpEntity<>(requestDTO.body(), requestDTO.headers());
                ResponseEntity<String> response = restTemplate.exchange(
                        requestDTO.targetUrl(),
                        requestDTO.method(),
                        requestEntity,
                        String.class
                );
                log.info("Request forwarded to {} with response status: {}", requestDTO.targetUrl(), response.getStatusCode());
                return response;
            } catch (Exception ex) {
                log.error("Failed to forward request to {}: {}", requestDTO.targetUrl(), ex.getMessage());
                throw new RuntimeException("Failed to forward request", ex);
            }
        }, virtualThreadExecutor);
    }
}