package com.itm.api_gateway.web.usergateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(
        description = "Payload para actualizar un usuario",
        example = "{\"id\":1,\"email\":\"nuevo-email@test.com\",\"role\":\"ADMIN\",\"active\":true}"
)
public record UpdateUserRequest(
        @Schema(description = "ID del usuario", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El ID es obligatorio")
        @Min(value = 1, message = "El ID debe ser mayor a 0")
        Long id,
        @Schema(description = "Email del usuario", example = "nuevo-email@test.com")
        @Email(message = "El email debe tener formato válido")
        String email,
        @Schema(description = "Rol del usuario", example = "ADMIN", allowableValues = {"CLIENT", "ADMIN"})
        String role,
        @Schema(description = "Estado activo del usuario", example = "true")
        Boolean active
) {
}