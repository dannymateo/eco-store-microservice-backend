package com.itm.eco_store.cart.application.port.out;

import com.itm.eco_store.cart.domain.model.Cart;

import java.time.Duration;
import java.util.Optional;

public interface CartRepositoryPort {

    Cart save(Cart cart, Duration ttl);

    Optional<Cart> findById(String cartId);
}
