package com.tyordanovv.load_balancer.lb.model;

import lombok.Data;

@Data
public class ServerStats {
    private String url;
    private double workload;
    private boolean healthy;
    // TODO add latency in last hour
    // TODO add categories

    public ServerStats(String url) {
        this.url = url;
        this.workload = 0;
        this.healthy = true;
    }
}