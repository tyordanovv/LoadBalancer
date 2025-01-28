package com.tyordanovv.load_balancer.lb.controller;

import com.tyordanovv.load_balancer.lb.model.RequestForwardingDto;
import com.tyordanovv.load_balancer.lb.model.ServerStats;
import com.tyordanovv.load_balancer.lb.service.LoadBalancerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
@Slf4j
public class LoadBalancerController {

    private final LoadBalancerService loadBalancerService;

    public LoadBalancerController(LoadBalancerService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }
    @RequestMapping("/**")
    public CompletableFuture<ResponseEntity<String>> routeRequest(
            @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) String body,
            HttpMethod method,
            HttpServletRequest request
    ) {
        ServerStats selectedServer = loadBalancerService.selectServer();

        if (selectedServer == null) {
            return CompletableFuture.completedFuture(ResponseEntity.status(503).body("No available server!"));
        }

        String targetUrl = selectedServer.getUrl() + request.getRequestURI();
        RequestForwardingDto forwardingDto = new RequestForwardingDto(targetUrl, headers, body, method);

        return loadBalancerService.forwardRequestAsync(forwardingDto)
                .thenApply(response -> {
                    log.info("Successful redirecting of the request.");
                    return response;
                })
                .exceptionally(ex -> {
                    log.error("Error occurred while forwarding the request", ex);
                    return ResponseEntity.status(500).body("Internal Server Error");
                });
    }
}
