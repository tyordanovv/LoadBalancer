package com.tyordanovv.load_balancer.service;

import com.tyordanovv.load_balancer.model.ServerStats;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BackendServerPool implements ServerPool {
    private final Map<String, ServerStats> servers = new ConcurrentHashMap<>();

    @Override
    public void addServer(String serverUrl) {
        servers.put(serverUrl, new ServerStats(serverUrl));
    }

    @Override
    public void removeServer(String serverUrl) {

    }

    @Override
    public List<ServerStats> getAllServers() {
        return servers.values().stream().toList();
    }

    @Override
    public void updateServerWorkload(String serverUrl, double workload) {

    }
}