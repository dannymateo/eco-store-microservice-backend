package com.itm.api_gateway.web.productgateway.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String name,
        String description,
        @NotBlank(message = "La categoria es obligatoria")
        String category,
        @NotNull(message = "El precio original es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser >= 0")
        BigDecimal originalPrice
) {
}
