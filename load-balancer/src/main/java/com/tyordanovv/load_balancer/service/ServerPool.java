package com.tyordanovv.load_balancer.service;

import com.tyordanovv.load_balancer.model.ServerStats;

import java.util.List;

public interface ServerPool {
    void addServer(String serverUrl);
    void removeServer(String serverUrl);
    List<ServerStats> getAllServers();
    void updateServerWorkload(String serverUrl, double workload);
}
