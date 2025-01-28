package com.tyordanovv.load_balancer.lb.service;

import com.tyordanovv.load_balancer.lb.model.ServerStats;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServerPool{
    private final Map<String, ServerStats> servers = new ConcurrentHashMap<>();

    public void addServer(String serverUrl) {
        servers.put(serverUrl, new ServerStats(serverUrl));
    }

    public void removeServer(String serverUrl) {

    }

    public List<ServerStats> getAllServers() {
        return servers.values().stream().toList();
    }

    public void updateServerWorkload(String serverUrl, double workload) {

    }
}