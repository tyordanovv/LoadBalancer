package com.tyordanovv.load_balancer.healthcheck.utils;

public class HealthCheckUtils {
    public static final String HEALTHY_SERVER = "Server on endpoint {} is healthy.";
    public static final String NO_METRICS = "No metrics received from server";
    public static final String UNHEALTHY_SERVER = "Server on endpoint {} is unhealthy: {}";
    public static final String HEALTH_CHECK_URL = "%s/health";
}
