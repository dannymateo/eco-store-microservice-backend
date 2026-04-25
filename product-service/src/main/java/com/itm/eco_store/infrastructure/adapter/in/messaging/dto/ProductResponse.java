package com.itm.eco_store.infrastructure.adapter.in.messaging.dto;

import com.itm.eco_store.domain.model.Category;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Category category,
        BigDecimal originalPrice,
        BigDecimal discountPercent,
        BigDecimal finalPrice,
        Integer stock
) {
}
