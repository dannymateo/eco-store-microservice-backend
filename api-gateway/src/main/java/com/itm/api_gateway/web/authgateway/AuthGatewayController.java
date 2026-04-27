package com.itm.api_gateway.web.authgateway;

import com.itm.api_gateway.config.NatsGatewayProperties;
import com.itm.api_gateway.messaging.NatsRequestClient;
import com.itm.api_gateway.messaging.NatsResponse;
import com.itm.api_gateway.web.authgateway.dto.ForgotPasswordRequest;
import com.itm.api_gateway.web.authgateway.dto.LoginRequest;
import com.itm.api_gateway.web.authgateway.dto.RegisterRequest;
import com.itm.api_gateway.web.authgateway.dto.ResetPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para autenticación de usuarios")
public class AuthGatewayController {

    private final NatsRequestClient natsRequestClient;
    private final NatsGatewayProperties properties;

    public AuthGatewayController(NatsRequestClient natsRequestClient, NatsGatewayProperties properties) {
        this.natsRequestClient = natsRequestClient;
        this.properties = properties;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario y retorna un token JWT"
    )
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest body) {
        NatsResponse response = natsRequestClient.request(properties.subject().auth().login(), Map.of(
                "email", body.email(),
                "password", body.password()
        ));
        return toHttpResponse(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    @Operation(
            summary = "Registrar usuario",
            description = "Registra un nuevo usuario en el sistema"
    )
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest body) {
        NatsResponse response = natsRequestClient.request(properties.subject().auth().register(), Map.of(
                "email", body.email(),
                "password", body.password(),
                "role", body.role()
        ));
        return toHttpResponse(response, HttpStatus.CREATED);
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Solicitar recuperación de contraseña",
            description = "Envía un email con enlace para restablecer contraseña"
    )
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest body) {
        NatsResponse response = natsRequestClient.request(properties.subject().auth().forgotPassword(), Map.of(
                "email", body.email()
        ));
        return toHttpResponse(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Restablecer contraseña",
            description = "Restablece la contraseña usando el token recibido por email"
    )
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest body) {
        NatsResponse response = natsRequestClient.request(properties.subject().auth().resetPassword(), Map.of(
                "token", body.token(),
                "newPassword", body.newPassword()
        ));
        return toHttpResponse(response, HttpStatus.OK);
    }

    private ResponseEntity<?> toHttpResponse(NatsResponse response, HttpStatus successStatus) {
        if (response.success()) {
            return ResponseEntity.status(successStatus).body(response.data());
        }
        HttpStatus status = mapErrorStatus(response.error());
        return ResponseEntity.status(status).body(Map.of("error", response.error()));
    }

    private HttpStatus mapErrorStatus(String error) {
        if (error == null || error.isBlank()) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String lowered = error.toLowerCase();
        if (lowered.contains("no encontrado")) {
            return HttpStatus.NOT_FOUND;
        }
        if (lowered.contains("ya existe") || lowered.contains("ya está registrado")) {
            return HttpStatus.CONFLICT;
        }
        if (lowered.contains("obligatorio") || lowered.contains("invalido") || lowered.contains("inválido") || lowered.contains("inválida")) {
            return HttpStatus.BAD_REQUEST;
        }
        if (lowered.contains("expirado") || lowered.contains("token")) {
            return HttpStatus.UNAUTHORIZED;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}