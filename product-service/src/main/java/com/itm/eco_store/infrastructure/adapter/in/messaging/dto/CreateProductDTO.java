package com.itm.eco_store.infrastructure.adapter.in.messaging.dto;

import com.itm.eco_store.domain.model.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductDTO(
        @NotNull(message = "El nombre es obligatorio")
        String name,

        String description,

        @NotNull(message = "La categoría es obligatoria")
        Category category,

        @NotNull(message = "El precio original es obligatorio")
        @DecimalMin(value = "0", inclusive = true, message = "El precio debe ser >= 0")
        BigDecimal originalPrice,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock debe ser >= 0")
        Integer stock
) {
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public Integer getStock() { return stock; }
}
