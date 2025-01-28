package com.tyordanovv.backend_server.connector.service;

import com.tyordanovv.backend_server.connector.model.ServerMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;

@Service
@Slf4j
public class LoadBalancerConnectorService {
    private final String serverId;

    public LoadBalancerConnectorService(ApplicationContext applicationContext) {
        this.serverId = applicationContext.getId();
    }


    public ServerMetrics getServerHealth() {
        log.info("Health check requested for serverId: {}.", serverId);
        return collectSystemMetrics();
    }

    private ServerMetrics collectSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        return ServerMetrics.builder()
                .cpuUsage(getCpuUsage())
                .memoryUsage(memoryUsed)
                .freeMemory(runtime.freeMemory())
                .totalMemory(runtime.totalMemory())
                .maxMemory(runtime.maxMemory())
                .serverId(serverId)
                .build();
    }

    private double getCpuUsage() {
        com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osBean.getCpuLoad() * 100;
    }
}
