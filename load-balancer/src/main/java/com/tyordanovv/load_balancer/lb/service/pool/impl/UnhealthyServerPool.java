package com.tyordanovv.load_balancer.lb.service.pool.impl;

import com.tyordanovv.load_balancer.lb.model.ServerStats;
import com.tyordanovv.load_balancer.lb.service.pool.ServerPool;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class UnhealthyServerPool implements ServerPool {
    private final Map<String, ServerStats> unhealthyServers = new ConcurrentHashMap<>();
    private final Duration maxRetentionPeriod = Duration.ofHours(24);

    @Override
    public void addServer(ServerStats server) {
        server.setLastFailureTime(LocalDateTime.now());
        unhealthyServers.put(server.getUrl(), server);
    }

    @Override
    public void removeServer(String serverUrl) {
        unhealthyServers.remove(serverUrl);
    }

    @Override
    public Optional<ServerStats> getServerByUrl(String serverUrl) {
        return Optional.ofNullable(unhealthyServers.get(serverUrl))
                .filter(this::isWithinRetentionPeriod);
    }

    @Override
    public List<ServerStats> getAllServers() {
        return unhealthyServers.values().stream()
                .filter(this::isWithinRetentionPeriod)
                .collect(Collectors.toList());
    }

    private boolean isWithinRetentionPeriod(ServerStats server) {
        return server.getLastFailureTime()
                .plus(maxRetentionPeriod)
                .isAfter(LocalDateTime.now());
    }
}
