package com.itm.eco_store.infrastructure.adapter.in.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nats")
public record NatsProductProperties(
        String url,
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
