package com.itm.api_gateway.web.authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Payload para restablecer contraseña",
        example = "{\"token\":\"uuid-del-token\",\"newPassword\":\"NuevaContrasena456!\"}"
)
public record ResetPasswordRequest(
        @Schema(description = "Token de recuperación", example = "uuid-del-token-recibido-en-email", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El token es obligatorio")
        String token,
        @Schema(description = "Nueva contraseña", example = "NuevaContrasena456!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "La nueva contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String newPassword
) {
}