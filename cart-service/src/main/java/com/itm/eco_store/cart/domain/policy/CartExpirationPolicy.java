package com.itm.eco_store.cart.domain.policy;

import com.itm.eco_store.cart.domain.model.Cart;

import java.time.Duration;

public final class CartExpirationPolicy {

    public static final Duration OPEN_CART_TTL = Duration.ofHours(24);

    private CartExpirationPolicy() {
    }

    public static Duration resolveTtl(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("El carrito es obligatorio");
        }
        return cart.isCheckedOut() ? null : OPEN_CART_TTL;
    }
}
