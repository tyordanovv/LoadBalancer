package com.tyordanovv.load_balancer.lb.model;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public record RequestForwardingDto (
        String targetUrl,
        HttpHeaders headers,
        String body,
        HttpMethod method
){}
