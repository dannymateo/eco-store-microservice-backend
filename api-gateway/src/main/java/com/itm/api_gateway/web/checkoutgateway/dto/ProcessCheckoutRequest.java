package com.itm.api_gateway.web.checkoutgateway.dto;

import jakarta.validation.constraints.NotBlank;

public record ProcessCheckoutRequest(
        @NotBlank(message = "El cartId es obligatorio")
        String cartId
) {
}