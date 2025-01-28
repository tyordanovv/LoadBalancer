package com.tyordanovv.load_balancer.healthcheck.service;

import com.tyordanovv.load_balancer.healthcheck.utils.HealthCheckUtils;
import com.tyordanovv.load_balancer.healthcheck.model.ServerMetrics;
import com.tyordanovv.load_balancer.lb.model.ServerStats;
import com.tyordanovv.load_balancer.lb.service.ServerPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HealthCheckService {
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckService.class);
    private final ServerPool serverPool;
    private final RestTemplate restTemplate;

    public HealthCheckService(
            ServerPool serverPool,
            RestTemplate restTemplate
    ) {
        this.serverPool = serverPool;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void performHealthChecks() {
        for (ServerStats server : serverPool.getAllServers()) {
            String serverEndpoint = String.format(HealthCheckUtils.HEALTH_CHECK_URL, server.getUrl());
            try {
                ResponseEntity<ServerMetrics> response = restTemplate.getForEntity(
                        serverEndpoint, ServerMetrics.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    ServerMetrics metrics = response.getBody();
                    server.setHealthy(true);
                    server.setWorkload(metrics.calculateWorkload());
                    LOG.info(HealthCheckUtils.HEALTHY_SERVER, serverEndpoint);

                    // TODO stream metrics to monitor
                } else {
                    handleUnhealthyServer(server, HealthCheckUtils.NO_METRICS);
                }
            } catch (Exception e) {
                handleUnhealthyServer(server, e.getMessage());
            }
        }
    }

    private void handleUnhealthyServer(ServerStats server, String reason) {
        LOG.error(HealthCheckUtils.UNHEALTHY_SERVER, server.getUrl(), reason);
        server.setHealthy(false);
        server.setWorkload(Double.MAX_VALUE);
    }
}
