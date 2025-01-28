package com.tyordanovv.backend_server.server.model;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public record ServerTestDto(
        String targetUrl,
        Integer returnCode,
        String message
){}
