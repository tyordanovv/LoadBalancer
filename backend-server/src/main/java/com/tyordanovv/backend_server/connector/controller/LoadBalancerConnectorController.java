package com.tyordanovv.backend_server.connector.controller;

import com.tyordanovv.backend_server.connector.model.ServerMetrics;
import com.tyordanovv.backend_server.connector.service.LoadBalancerConnectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health")
@Slf4j
public class LoadBalancerConnectorController {
    private final LoadBalancerConnectorService loadBalancerService;

    public LoadBalancerConnectorController(
            LoadBalancerConnectorService loadBalancerService
    ) {
        this.loadBalancerService = loadBalancerService;
    }
    @GetMapping()
    public ResponseEntity<ServerMetrics> healthCheck() {
        ServerMetrics metrics = loadBalancerService.getServerHealth();
        log.info("Server {} metrics: CPU usage = {}%, memory usage = {}%",
                metrics.getServerId(),
                String.format("%.2f", metrics.getCpuUsage()),
                String.format("%.2f", (double) metrics.getMemoryUsage() / metrics.getTotalMemory() * 100));
        return ResponseEntity.ok(metrics);
    }
}
