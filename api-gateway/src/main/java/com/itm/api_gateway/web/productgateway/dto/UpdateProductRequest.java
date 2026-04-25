package com.itm.api_gateway.web.productgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(
        description = "Payload para actualizar un producto existente",
        example = "{\"name\":\"Camiseta Eco Bamboo Premium\",\"description\":\"Version premium con costuras reforzadas\",\"category\":\"TEMPORADA_PASADA\",\"originalPrice\":99.90}"
)
public record UpdateProductRequest(
        @Schema(description = "Nombre del producto", example = "Camiseta Eco Bamboo Premium", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El nombre es obligatorio")
        String name,
        @Schema(description = "Descripcion del producto", example = "Version premium con costuras reforzadas")
        String description,
        @Schema(description = "Categoria del producto", example = "TEMPORADA_PASADA", allowableValues = {"NORMAL", "TEMPORADA_PASADA"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La categoria es obligatoria")
        String category,
        @Schema(description = "Precio original del producto", example = "99.90", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El precio original es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser >= 0")
        BigDecimal originalPrice
) {
}
