package com.itm.eco_store.infrastructure.adapter.in.messaging.dto;

import com.itm.eco_store.domain.model.Category;

import java.math.BigDecimal;

public record UpdateProductCommand(
        Long id,
        String name,
        String description,
        Category category,
        BigDecimal originalPrice,
        Integer stock
) {
}
