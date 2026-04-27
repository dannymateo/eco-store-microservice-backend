package com.itm.eco_store.checkout.infrastructure.adapter.in.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "nats")
public record NatsCheckoutProperties(
        String url,
        Subject subject
) {
    public record Subject(
            Cart cart,
            Product product,
            Checkout checkout
    ) {
    }

    public record Cart(
            String get
    ) {
    }

    public record Product(
            String get
    ) {
    }

    public record Checkout(
            String process,
            String confirm
    ) {
    }
}