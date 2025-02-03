package com.tyordanovv.load_balancer.lb.service.pool.impl;

import com.tyordanovv.load_balancer.lb.model.ServerStats;
import com.tyordanovv.load_balancer.lb.service.pool.ServerPool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HealthyServerPool implements ServerPool {
    private final Map<String, ServerStats> healthyServers = new ConcurrentHashMap<>();

    @Override
    public void addServer(ServerStats server) {
        healthyServers.put(server.getUrl(), server);
    }

    @Override
    public void removeServer(String serverUrl) {
        healthyServers.remove(serverUrl);
    }

    @Override
    public List<ServerStats> getAllServers() {
        return new ArrayList<>(healthyServers.values());
    }

    @Override
    public Optional<ServerStats> getServerByUrl(String url) {
        return Optional.ofNullable(healthyServers.get(url));
    }
}