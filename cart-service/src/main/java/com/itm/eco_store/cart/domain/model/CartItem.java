package com.itm.eco_store.cart.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    private ProductInfo product;
    private int quantity;

    public void increaseQuantity(int amount) {
        validateQuantity(amount);
        this.quantity += amount;
    }

    public Money lineTotal() {
        BigDecimal total = product.getPrice().getValue().multiply(BigDecimal.valueOf(quantity));
        return Money.of(total, product.getPrice().getCurrency());
    }

    private void validateQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
    }
}
