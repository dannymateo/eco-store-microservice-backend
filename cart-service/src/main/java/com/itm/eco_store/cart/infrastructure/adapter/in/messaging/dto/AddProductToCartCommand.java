package com.itm.eco_store.cart.infrastructure.adapter.in.messaging.dto;

public record AddProductToCartCommand(
        String cartId,
        Long productId,
        Integer quantity
) {
}
