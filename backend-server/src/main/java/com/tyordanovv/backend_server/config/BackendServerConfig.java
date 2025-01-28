package com.tyordanovv.backend_server.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Getter
@Component
@Slf4j
public class BackendServerConfig {
    @Value("${load-balancer.url}")
    private String loadBalancerUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}