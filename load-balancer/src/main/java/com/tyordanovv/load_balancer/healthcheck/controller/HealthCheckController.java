package com.tyordanovv.load_balancer.healthcheck.controller;

import com.tyordanovv.load_balancer.healthcheck.service.HealthCheckService;
import com.tyordanovv.load_balancer.lb.model.ServerStats;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthCheckController {
    private final HealthCheckService healthCheckService;

    public static final String SERVER_URL = "http://%s:%d";

    public HealthCheckController(
            HealthCheckService healthCheckService
    ) {
        this.healthCheckService = healthCheckService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerServer(HttpServletRequest request, @RequestParam int port) {
        String serverIp = extractServerIP(request);
        String serverAddress = String.format(SERVER_URL, serverIp, port);
        ServerStats registeredServer = healthCheckService.registerServer(serverAddress);
        return ResponseEntity.ok("Server registered: " + registeredServer.getUrl());
    }

    private String extractServerIP(HttpServletRequest request) {
        String serverIp = request.getHeader("X-Forwarded-For");
        if (serverIp == null || serverIp.isEmpty()) {
            serverIp = request.getRemoteAddr();
        }
        return serverIp;
    }
}
