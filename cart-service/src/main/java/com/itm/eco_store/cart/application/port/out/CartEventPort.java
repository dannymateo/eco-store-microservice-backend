package com.itm.eco_store.cart.application.port.out;

import com.itm.eco_store.cart.domain.model.Cart;

public interface CartEventPort {

    void publishCartCheckedOut(Cart cart);
}
