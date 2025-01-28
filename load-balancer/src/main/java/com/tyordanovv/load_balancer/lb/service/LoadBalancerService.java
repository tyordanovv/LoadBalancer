package com.tyordanovv.load_balancer.lb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoadBalancerService {
    private static final Logger log = LoggerFactory.getLogger(LoadBalancerService.class);
    private final ServerPool serverPool;

    public LoadBalancerService(ServerPool serverPool) {
        this.serverPool = serverPool;
    }

    public void addServer(String url) {
        try {
            serverPool.addServer(url);
            log.info("Server {} was successfully added to the pool.", url);
        } catch (Exception e) {
            log.info("Server {} could not be added to the pool. {}", url, e.getMessage());
        }
    }

    public void removeServer(String url) {
        serverPool.removeServer(url);
    }
}