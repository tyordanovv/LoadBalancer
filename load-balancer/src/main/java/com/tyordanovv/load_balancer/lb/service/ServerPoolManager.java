package com.tyordanovv.load_balancer.lb.service;

import com.tyordanovv.load_balancer.lb.model.ServerStats;
import com.tyordanovv.load_balancer.lb.service.pool.ServerPool;
import com.tyordanovv.load_balancer.lb.service.pool.impl.HealthyServerPool;
import com.tyordanovv.load_balancer.lb.service.pool.impl.UnhealthyServerPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Manages server pools ({@link HealthyServerPool} and {@link UnhealthyServerPool}).
 * - Moves servers between healthy and unhealthy pools.
 * - Registers new servers.
 * - Updates server statuses and workload.
 */
@Component
@Slf4j
public class ServerPoolManager {
    private final ServerPool healthyServerPool;
    private final ServerPool unhealthyServerPool;

    public ServerPoolManager(
            HealthyServerPool healthyServerPool,
            UnhealthyServerPool unhealthyServerPool
    ) {
        this.healthyServerPool = healthyServerPool;
        this.unhealthyServerPool = unhealthyServerPool;
    }

    /**
     * Moves a server from the healthy pool to the unhealthy pool.
     *
     * @param serverUrl URL of the server to be moved.
     */
    private void moveServerToUnhealthyPool(String serverUrl) {
        healthyServerPool.getServerByUrl(serverUrl).ifPresent(serverStats -> {
            healthyServerPool.removeServer(serverUrl);
            unhealthyServerPool.addServer(serverStats);
            log.warn("Server {} moved to Unhealthy Pool", serverUrl);
        });
    }

    /**
     * Moves a server from the unhealthy pool back to the healthy pool.
     *
     * @param serverUrl URL of the server to be moved.
     */
    public void moveServerToHealthyPool(String serverUrl) {
        unhealthyServerPool.getServerByUrl(serverUrl).ifPresent(serverStats -> {
            unhealthyServerPool.removeServer(serverUrl);
            healthyServerPool.addServer(serverStats);
            log.info("Server {} recovered and moved to Healthy Pool", serverUrl);
        });
    }

    /**
     * Marks a currently healthy server as unhealthy if it exceeds failure limits.
     *
     * @param serverUrl URL of the server to mark as unhealthy.
     */
    public void markHealthyServerAsUnhealthy(String serverUrl) {
        healthyServerPool.getServerByUrl(serverUrl).ifPresent(server -> {
            server.incrementFailure();
            if (server.exceedsConsecutiveFailures()) {
                log.warn("Server {} exceeded failure limit and is now Unhealthy", server.getUrl());
                moveServerToUnhealthyPool(serverUrl);
            }
        });
    }

    /**
     * Updates the workload of a healthy server and resets its failure counter.
     *
     * @param serverUrl URL of the server.
     * @param workload  New workload to be set.
     */
    public void updateHealthyServerStatus(String serverUrl, double workload) {
        healthyServerPool.getServerByUrl(serverUrl).ifPresent(server -> {
            server.setWorkload(workload);
            server.resetHealthStatus();
            log.info("Updated workload for server {}: {}", serverUrl, workload);
        });
    }

    /**
     * Retrieves all servers from either the healthy or unhealthy pool.
     *
     * @param isHealthy If true, returns list of healthy servers; otherwise, returns unhealthy servers.
     * @return List of server statistics {@link ServerStats}.
     */
    public List<ServerStats> getAllServers(boolean isHealthy) {
        return isHealthy ? healthyServerPool.getAllServers() : unhealthyServerPool.getAllServers();
    }

    /**
     * Registers a new server in the healthy pool if it is not already registered.
     *
     * @param serverAddress URL of the new server.
     * @return The registered server's statistics {@link ServerStats}.
     */
    public ServerStats registerHealthyServer(String serverAddress) {
        Optional<ServerStats> existingServer = healthyServerPool.getServerByUrl(serverAddress);

        if (existingServer.isPresent()) {
            log.info("Server {} is already registered", serverAddress);
            return existingServer.get();
        }

        ServerStats newServer = new ServerStats(serverAddress);
        healthyServerPool.addServer(newServer);
        log.info("New server registered: {}", serverAddress);

        return newServer;
    }
}