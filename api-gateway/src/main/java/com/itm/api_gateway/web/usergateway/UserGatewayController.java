package com.itm.api_gateway.web.usergateway;

import com.itm.api_gateway.config.NatsGatewayProperties;
import com.itm.api_gateway.messaging.NatsRequestClient;
import com.itm.api_gateway.messaging.NatsResponse;
import com.itm.api_gateway.web.usergateway.dto.CreateUserRequest;
import com.itm.api_gateway.web.usergateway.dto.UpdateUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Gestión de Usuarios", description = "Endpoints para administración de usuarios")
public class UserGatewayController {

    private final NatsRequestClient natsRequestClient;
    private final NatsGatewayProperties properties;

    public UserGatewayController(NatsRequestClient natsRequestClient, NatsGatewayProperties properties) {
        this.natsRequestClient = natsRequestClient;
        this.properties = properties;
    }

    @PostMapping
    @Operation(
            summary = "Crear usuario",
            description = "Crea un nuevo usuario en el sistema (requiere autenticación)"
    )
    public ResponseEntity<?> create(@Valid @RequestBody CreateUserRequest body) {
        NatsResponse response = natsRequestClient.request(properties.subject().user().create(), Map.of(
                "email", body.email(),
                "password", body.password(),
                "role", body.role()
        ));
        return toHttpResponse(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Consulta un usuario existente por su identificador"
    )
    public ResponseEntity<?> getById(@Parameter(example = "1") @PathVariable Long id) {
        NatsResponse response = natsRequestClient.request(properties.subject().user().get(), Map.of("id", id));
        return toHttpResponse(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente"
    )
    public ResponseEntity<?> update(@Parameter(example = "1") @PathVariable Long id, @Valid @RequestBody UpdateUserRequest body) {
        Map<String, Object> command = new HashMap<>();
        command.put("id", id);
        if (body.email() != null) {
            command.put("email", body.email());
        }
        if (body.role() != null) {
            command.put("role", body.role());
        }
        if (body.active() != null) {
            command.put("active", body.active());
        }
        NatsResponse response = natsRequestClient.request(properties.subject().user().update(), command);
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
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}