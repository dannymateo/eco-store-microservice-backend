package com.itm.eco_store.checkout.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Checkout {

    private Long id;
    private String cartId;
    private CheckoutStatus status;
    private List<CheckoutItem> items;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentTransactionId;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;

    public static Checkout create(String cartId, List<CheckoutItem> items) {
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException("El cartId es obligatorio");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("No se puede crear un checkout sin items");
        }

        BigDecimal total = items.stream()
                .map(CheckoutItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String currency = "USD";

        return new Checkout(
                null,
                cartId.trim(),
                CheckoutStatus.PENDING,
                new ArrayList<>(items),
                total,
                currency,
                null,
                LocalDateTime.now(),
                null
        );
    }

    public void confirm(String transactionId) {
        if (status != CheckoutStatus.PENDING) {
            throw new IllegalStateException("Solo se pueden confirmar checkouts en estado PENDING. Estado actual: " + status);
        }
        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException("El transactionId es obligatorio");
        }
        this.status = CheckoutStatus.CONFIRMED;
        this.paymentTransactionId = transactionId;
        this.confirmedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = CheckoutStatus.FAILED;
    }
}