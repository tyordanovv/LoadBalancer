package com.tyordanovv.load_balancer.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HealthCheckService {
    private final BackendServerPool serverPool;
    private final RestTemplate restTemplate = new RestTemplate();

    public HealthCheckService(BackendServerPool serverPool) {
        this.serverPool = serverPool;
    }

    // TODO performHealthChecks
}
