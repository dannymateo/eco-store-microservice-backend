package com.itm.api_gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nats")
public record NatsGatewayProperties(
        String url,
        long requestTimeoutMs,
        Subject subject
) {
    public record Subject(
            Product product,
            Cart cart,
            Checkout checkout,
            Auth auth,
            User user
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

    public record Cart(
            String addProduct,
            String removeProduct,
            String get,
            String checkout
    ) {
    }

    public record Checkout(
            String process,
            String confirm
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
