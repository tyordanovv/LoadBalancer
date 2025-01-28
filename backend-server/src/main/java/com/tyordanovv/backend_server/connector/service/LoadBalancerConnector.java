package com.tyordanovv.backend_server.connector.service;

import com.tyordanovv.backend_server.config.BackendServerConfig;
import com.tyordanovv.backend_server.connector.utils.LoadBalancerConnectorUtils;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class LoadBalancerConnector {
    private final BackendServerConfig serverConfig;
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService executorService;


    public LoadBalancerConnector(
            BackendServerConfig serverConfig,
            RestTemplate restTemplate
    ) {
        this.serverConfig = serverConfig;
        this.restTemplate = restTemplate;
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @EventListener
    public void onApplicationEvent(final ServletWebServerInitializedEvent event) {
        int serverPort = event.getWebServer().getPort();
        log.info(LoadBalancerConnectorUtils.PORT_SET, serverPort);
        registerWithLoadBalancer(serverPort);
    }

    private void registerWithLoadBalancer(int serverPort) {
        executorService.scheduleAtFixedRate(() -> {
            String registerEndpoint = String.format(
                    LoadBalancerConnectorUtils.LB_REGISTER_URL,
                    serverConfig.getLoadBalancerUrl(), serverPort);
            try {
                restTemplate.postForEntity(registerEndpoint, null, String.class);
                log.info(LoadBalancerConnectorUtils.LB_SUCCESSFUL_REGISTER, registerEndpoint, serverPort);
                executorService.shutdown();
            } catch (Exception e) {
                log.warn(LoadBalancerConnectorUtils.LB_UNSUCCESSFUL_REGISTER, registerEndpoint, serverPort, e);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }
}
