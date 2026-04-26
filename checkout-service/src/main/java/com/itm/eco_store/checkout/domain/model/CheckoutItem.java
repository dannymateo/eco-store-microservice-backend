package com.itm.eco_store.checkout.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutItem {

    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal lineTotal;

    public static CheckoutItem of(Long productId, String productName, BigDecimal unitPrice, int quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("El productId es obligatorio");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario debe ser >= 0");
        }
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return new CheckoutItem(productId, productName, unitPrice, quantity, lineTotal);
    }
}