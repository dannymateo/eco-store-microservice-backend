package com.itm.api_gateway.web.productgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(
        description = "Payload para crear un producto en el catalogo",
        example = "{\"name\":\"Camiseta Eco Bamboo\",\"description\":\"Camiseta de fibra de bamboo, talla M\",\"category\":\"NORMAL\",\"originalPrice\":79.90}"
)
public record CreateProductRequest(
        @Schema(description = "Nombre del producto", example = "Camiseta Eco Bamboo", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El nombre es obligatorio")
        String name,
        @Schema(description = "Descripcion del producto", example = "Camiseta de fibra de bamboo, talla M")
        String description,
        @Schema(description = "Categoria del producto", example = "NORMAL", allowableValues = {"NORMAL", "TEMPORADA_PASADA"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La categoria es obligatoria")
        String category,
        @Schema(description = "Precio original del producto", example = "79.90", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El precio original es obligatorio")
        @DecimalMin(value = "0.0", inclusive = true, message = "El precio debe ser >= 0")
        BigDecimal originalPrice
) {
}
