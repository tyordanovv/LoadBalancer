package com.tyordanovv.backend_server;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Getter
@Component
public class BackendServerConfig {
    @Value("${load-balancer.url}")
    private String loadBalancerUrl;

    private int serverPort;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    private void init() {
        WebServer webServer = ((WebServerApplicationContext) applicationContext).getWebServer();
        serverPort = webServer.getPort();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}