package com.tyordanovv.load_balancer.lb.service.pool;

import com.tyordanovv.load_balancer.lb.model.ServerStats;

import java.util.List;
import java.util.Optional;

public interface ServerPool {
    void addServer(ServerStats serverUrl);
    void removeServer(String serverUrl);
    List<ServerStats> getAllServers();
    Optional<ServerStats> getServerByUrl(String url);
}