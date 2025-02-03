package com.tyordanovv.backend_server.server.model;

public record ServerTestDto(
        String targetUrl,
        Integer returnCode,
        String message
){}
