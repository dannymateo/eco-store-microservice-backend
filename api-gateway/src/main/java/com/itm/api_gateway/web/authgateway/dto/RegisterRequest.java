package com.itm.api_gateway.web.authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Payload para registrar un nuevo usuario",
        example = "{\"email\":\"nuevousuario@test.com\",\"password\":\"Password123!\",\"role\":\"CLIENT\"}"
)
public record RegisterRequest(
        @Schema(description = "Email del usuario", example = "nuevousuario@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener formato válido")
        String email,
        @Schema(description = "Contraseña del usuario", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,
        @Schema(description = "Rol del usuario", example = "CLIENT", allowableValues = {"CLIENT", "ADMIN"})
        @NotBlank(message = "El rol es obligatorio")
        String role
) {
}