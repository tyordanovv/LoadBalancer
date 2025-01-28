package com.tyordanovv.backend_server.connector.utils;

public class LoadBalancerConnectorUtils {
    public static final String PORT_SET = "Program port is set to {}";
    public static final String LB_SUCCESSFUL_REGISTER = "Successfully registered with Load Balancer at {} with port {}";
    public static final String LB_UNSUCCESSFUL_REGISTER = "Failed to register with Load Balancer at {} with port {}. Retrying in 10 seconds...";
    public static final String LB_REGISTER_URL = "%s/health/register?port=%d";
}
