package com.itm.eco_store.checkout.infrastructure.adapter.in.messaging.dto;

public record ConfirmCheckoutCommand(
        Long checkoutId
) {
}