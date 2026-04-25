package com.itm.api_gateway.web.cartgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Payload para agregar producto al carrito",
        example = "{\"productId\":1,\"quantity\":2}"
)
public record AddProductToCartRequest(
        @Schema(description = "Id del producto", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El id del producto es obligatorio")
        Long productId,
        @Schema(description = "Cantidad de productos", example = "2", minimum = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        Integer quantity
) {
}
