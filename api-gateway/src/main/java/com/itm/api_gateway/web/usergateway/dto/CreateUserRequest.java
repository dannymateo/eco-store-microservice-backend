package com.itm.api_gateway.web.usergateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Payload para crear un usuario",
        example = "{\"email\":\"admin@test.com\",\"password\":\"AdminPass123!\",\"role\":\"ADMIN\"}"
)
public record CreateUserRequest(
        @Schema(description = "Email del usuario", example = "admin@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener formato válido")
        String email,
        @Schema(description = "Contraseña del usuario", example = "AdminPass123!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,
        @Schema(description = "Rol del usuario", example = "ADMIN", allowableValues = {"CLIENT", "ADMIN"})
        @NotBlank(message = "El rol es obligatorio")
        String role
) {
}