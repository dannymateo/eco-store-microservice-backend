package com.itm.eco_store.cart.infrastructure.adapter.in.messaging.dto;

public record RemoveProductFromCartCommand(
        String cartId,
        Long productId
) {
}
