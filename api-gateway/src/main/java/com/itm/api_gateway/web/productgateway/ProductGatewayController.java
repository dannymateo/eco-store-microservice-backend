package com.itm.api_gateway.web.productgateway;

import com.itm.api_gateway.config.NatsGatewayProperties;
import com.itm.api_gateway.messaging.NatsRequestClient;
import com.itm.api_gateway.messaging.NatsResponse;
import com.itm.api_gateway.web.productgateway.dto.CreateProductRequest;
import com.itm.api_gateway.web.productgateway.dto.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/products")
public class ProductGatewayController {

    private final NatsRequestClient natsRequestClient;
    private final NatsGatewayProperties properties;

    public ProductGatewayController(NatsRequestClient natsRequestClient, NatsGatewayProperties properties) {
        this.natsRequestClient = natsRequestClient;
        this.properties = properties;
    }

    @PostMapping
    @Operation(
            summary = "Crear producto",
            description = "Crea un producto en el catalogo. Ejemplo real: categoria NORMAL o TEMPORADA_PASADA y precio decimal mayor o igual a 0."
    )
    public ResponseEntity<?> create(@Valid @RequestBody CreateProductRequest body) {
        NatsResponse response = natsRequestClient.request(properties.subject().product().create(), body);
        return toHttpResponse(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Consulta un producto existente por su identificador.")
    public ResponseEntity<?> getById(@Parameter(example = "1") @PathVariable Long id) {
        NatsResponse response = natsRequestClient.request(properties.subject().product().get(), Map.of("id", id));
        return toHttpResponse(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Retorna todos los productos del catalogo.")
    public ResponseEntity<?> getAll() {
        NatsResponse response = natsRequestClient.request(properties.subject().product().list(), Map.of());
        return toHttpResponse(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar producto",
            description = "Actualiza nombre, descripcion, categoria y precio original de un producto existente."
    )
    public ResponseEntity<?> update(@Parameter(example = "1") @PathVariable Long id, @Valid @RequestBody UpdateProductRequest body) {
        Map<String, Object> command = new HashMap<>();
        command.put("id", id);
        command.put("name", body.name());
        command.put("description", body.description());
        command.put("category", body.category());
        command.put("originalPrice", body.originalPrice());
        NatsResponse response = natsRequestClient.request(properties.subject().product().update(), command);
        return toHttpResponse(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID.")
    public ResponseEntity<?> delete(@Parameter(example = "1") @PathVariable Long id) {
        NatsResponse response = natsRequestClient.request(properties.subject().product().delete(), Map.of("id", id));
        return toHttpResponse(response, HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<?> toHttpResponse(NatsResponse response, HttpStatus successStatus) {
        if (response.success()) {
            if (successStatus == HttpStatus.NO_CONTENT) {
                return ResponseEntity.noContent().build();
            }
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
        if (lowered.contains("ya existe")) {
            return HttpStatus.CONFLICT;
        }
        if (lowered.contains("obligatorio") || lowered.contains("invalido") || lowered.contains("inválido")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
