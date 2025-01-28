package com.tyordanovv.load_balancer.healthcheck.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder // TODO move to different module
public class ServerMetrics {
    private String serverId;
    private double cpuUsage;
    private long memoryUsage;
    private long freeMemory;
    private long totalMemory;
    private long maxMemory;

    public double calculateWorkload() {
        // Calculate workload based on CPU and memory usage
        double memoryUsagePercentage = (double) memoryUsage / totalMemory * 100;
        return (cpuUsage + memoryUsagePercentage) / 2;
    }
}
