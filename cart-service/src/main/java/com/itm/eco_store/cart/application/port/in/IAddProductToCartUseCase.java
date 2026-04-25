package com.itm.eco_store.cart.application.port.in;

import com.itm.eco_store.cart.domain.model.Cart;

public interface IAddProductToCartUseCase {

    Cart addProduct(String cartId, Long productId, int quantity);
}
