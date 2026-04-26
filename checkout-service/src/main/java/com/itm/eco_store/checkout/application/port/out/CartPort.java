package com.itm.eco_store.checkout.application.port.out;

import java.util.List;
import java.util.Optional;

public interface CartPort {

    Optional<CartInfo> getCartById(String cartId);

    record CartInfo(String id, boolean checkedOut, List<CartItemInfo> items) {}

    record CartItemInfo(Long productId, int quantity) {}
}