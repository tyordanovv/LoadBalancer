package com.tyordanovv.load_balancer.healthcheck.controller;

import com.tyordanovv.load_balancer.lb.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {
    private final LoadBalancerService loadBalancerService;

    public HealthCheckController(
            LoadBalancerService loadBalancerService
    ) {
        this.loadBalancerService = loadBalancerService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerServer(HttpServletRequest request, @RequestParam int port) {
        String serverIp = extractServerIP(request);
        String serverAddress = String.format("http://%s:%d", serverIp, port);
        loadBalancerService.addServer(serverAddress);
        return ResponseEntity.ok("Server registered: " + serverAddress);
    }

    private String extractServerIP(HttpServletRequest request) {
        String serverIp = request.getHeader("X-Forwarded-For");
        if (serverIp == null || serverIp.isEmpty()) {
            serverIp = request.getRemoteAddr();
        }
        return serverIp;
    }
}
