package com.tyordanovv.load_balancer.model;

import lombok.Data;

@Data
public class ServerStats {
    private final String url;
    private double workload;
    private boolean healthy;

    public ServerStats(String url) {
        this.url = url;
        this.workload = 0;
        this.healthy = true;
    }
}