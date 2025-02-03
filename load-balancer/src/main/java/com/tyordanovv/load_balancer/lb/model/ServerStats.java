package com.tyordanovv.load_balancer.lb.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServerStats {
    private String url;
    private double workload;
    private int consecutiveFailures;
    private LocalDateTime lastFailureTime;

    private static final int MAX_CONSECUTIVE_FAILURES = 3;

    public ServerStats(String url) {
        this.url = url;
        this.workload = 0.0;
        this.consecutiveFailures = 0;
    }

    public void incrementFailure() {
        lastFailureTime = LocalDateTime.now();
        consecutiveFailures++;
    }

    public void resetHealthStatus() {
        consecutiveFailures = 0;
    }

    public boolean exceedsConsecutiveFailures() {
        return consecutiveFailures >= MAX_CONSECUTIVE_FAILURES;
    }
}