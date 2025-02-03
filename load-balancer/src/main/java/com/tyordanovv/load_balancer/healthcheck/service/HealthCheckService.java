package com.tyordanovv.load_balancer.healthcheck.service;

import com.tyordanovv.load_balancer.healthcheck.model.ServerMetrics;
import com.tyordanovv.load_balancer.lb.model.ServerStats;
import com.tyordanovv.load_balancer.lb.service.ServerPoolManager;
import com.tyordanovv.load_balancer.lb.service.pool.impl.UnhealthyServerPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Service responsible for monitoring server health and handling server state transitions.
 * - Periodically checks unhealthy servers to see if they have recovered.
 * - Monitors healthy servers and updates their workload.
 * - Handles registering new servers.
 */
@Component
@Slf4j
public class HealthCheckService {
    private static final String HEALTH_CHECK_URL = "http://%s/health";
    private static final String UNHEALTHY_SERVER = "Server {} is unhealthy due to {}";
    private static final String RECOVERED_SERVER = "Server {} has recovered";

    private final ServerPoolManager serverPoolManager;
    private final RestTemplate restTemplate;

    public HealthCheckService(ServerPoolManager serverPoolManager, RestTemplate restTemplate) {
        this.serverPoolManager = serverPoolManager;
        this.restTemplate = restTemplate;
    }

    private void markServerAsUnhealthy(String serverUrl, String reason) {
        log.error(UNHEALTHY_SERVER, serverUrl, reason);
        serverPoolManager.markHealthyServerAsUnhealthy(serverUrl);
    }

    /**
     * Periodically checks the {@link UnhealthyServerPool} for unhealthy servers to see if they have recovered.
     * - The retention period for unhealthy servers is 24 hours.
     * Runs every 30 minutes.
     */
    @Scheduled(fixedDelay = 1800000)
    public void checkUnhealthyServers() {
        serverPoolManager.getAllServers(false).forEach(server -> {
            String serverEndpoint = String.format(HEALTH_CHECK_URL, server.getUrl());
            try {
                ResponseEntity<ServerMetrics> response = restTemplate.getForEntity(serverEndpoint, ServerMetrics.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    serverPoolManager.moveServerToHealthyPool(server.getUrl());
                    log.info(RECOVERED_SERVER, serverEndpoint);
                }
            } catch (Exception e) {
                log.error("Failed to check unhealthy server {}: {}", serverEndpoint, e.getMessage(), e);
            }
        });
    }

    /**
     * Periodically checks healthy servers to:
     * - Ensure they are still healthy.
     * - Update their workload.
     * Runs every 5 seconds.
     */
    @Scheduled(fixedDelay = 5000)
    public void checkHealthyServers() {
        serverPoolManager.getAllServers(true).forEach(server -> {
            String serverEndpoint = String.format(HEALTH_CHECK_URL, server.getUrl());
            try {
                ResponseEntity<ServerMetrics> response = restTemplate.getForEntity(serverEndpoint, ServerMetrics.class);
                ServerMetrics metrics = response.getBody();

                if (response.getStatusCode().is2xxSuccessful() && metrics != null) {
                    serverPoolManager.updateHealthyServerStatus(metrics.getServerId(), metrics.calculateWorkload());
                    server.setWorkload(metrics.calculateWorkload());
                    log.info("Server {} is healthy", serverEndpoint);
                } else {
                    markServerAsUnhealthy(server.getUrl(), "No valid metrics received");
                }
            } catch (Exception e) {
                markServerAsUnhealthy(server.getUrl(), e.getMessage());
            }
        });
    }

    public ServerStats registerServer(String serverAddress) {
        ServerStats server = serverPoolManager.registerHealthyServer(serverAddress);
        log.info("New server registered: {}", serverAddress);
        return server;
    }
}
