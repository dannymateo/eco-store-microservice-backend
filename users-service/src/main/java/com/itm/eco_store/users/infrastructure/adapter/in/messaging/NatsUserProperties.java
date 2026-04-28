package com.itm.eco_store.users.infrastructure.adapter.in.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nats")
public record NatsUserProperties(
        String url,
        Subject subject
) {
    public record Subject(
            Auth auth,
            User user
    ) {
    }

    public record Auth(
            String login,
            String register,
            String forgotPassword,
            String resetPassword
    ) {
    }

    public record User(
            String create,
            String get,
            String update
    ) {
    }
}