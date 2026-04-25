package com.itm.api_gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nats")
public record NatsGatewayProperties(
        String url,
        long requestTimeoutMs,
        Subject subject
) {
    public record Subject(
            Product product
    ) {
    }

    public record Product(
            String create,
            String get,
            String list,
            String update,
            String delete
    ) {
    }
}
