package com.itm.eco_store.cart.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Money {

    private BigDecimal value;
    private String currency;

    public static Money of(BigDecimal value, String currency) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El valor monetario debe ser >= 0");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }
        return new Money(value.setScale(2, RoundingMode.HALF_UP), currency.trim().toUpperCase());
    }
}
