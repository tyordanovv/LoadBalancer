package com.tyordanovv.backend_server;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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

    public LoadBalancerConnector(
            BackendServerConfig serverConfig,
            RestTemplate restTemplate
    ) {
        this.serverConfig = serverConfig;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void registerWithLoadBalancer() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            String registerEndpoint = serverConfig.getLoadBalancerUrl() + "/api/register?port=" + serverConfig.getServerPort();
            try {
                restTemplate.postForEntity(registerEndpoint, null, String.class);
                log.info("Successfully registered with Load Balancer at {} with port {}",
                        registerEndpoint, serverConfig.getServerPort());
                executorService.shutdown();
            } catch (Exception e) {
                log.warn("Failed to register with Load Balancer at {} with port {}. Retrying in 10 seconds...",
                        registerEndpoint, serverConfig.getServerPort(), e);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
}
