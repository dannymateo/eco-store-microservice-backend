package com.itm.api_gateway.web.authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(
        description = "Payload para iniciar sesión",
        example = "{\"email\":\"usuario@test.com\",\"password\":\"Password123!\"}"
)
public record LoginRequest(
        @Schema(description = "Email del usuario", example = "usuario@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener formato válido")
        String email,
        @Schema(description = "Contraseña del usuario", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La contraseña es obligatorio")
        String password
) {
}