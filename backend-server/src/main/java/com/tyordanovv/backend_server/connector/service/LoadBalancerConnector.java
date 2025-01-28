package com.tyordanovv.backend_server.connector.service;

import com.tyordanovv.backend_server.config.BackendServerConfig;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LoadBalancerConnector {
    private final BackendServerConfig serverConfig;
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService executorService;


    public LoadBalancerConnector(
            BackendServerConfig serverConfig,
            RestTemplate restTemplate
    ) {
        this.serverConfig = serverConfig;
        this.restTemplate = restTemplate;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        int serverPort = event.getWebServer().getPort();
        log.info("Program port is set to {}", serverPort);
        registerWithLoadBalancer(serverPort);
    }

    private void registerWithLoadBalancer(int serverPort) {
        executorService.scheduleAtFixedRate(() -> {
            String registerEndpoint = serverConfig.getLoadBalancerUrl() + "/api/register?port=" + serverPort;
            try {
                restTemplate.postForEntity(registerEndpoint, null, String.class);
                log.info("Successfully registered with Load Balancer at {} with port {}",
                        registerEndpoint, serverPort);
                executorService.shutdown();
            } catch (Exception e) {
                log.warn("Failed to register with Load Balancer at {} with port {}. Retrying in 10 seconds...",
                        registerEndpoint, serverPort, e);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }
}
