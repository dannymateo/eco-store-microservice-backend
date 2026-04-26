package com.itm.api_gateway.web.checkoutgateway;

import com.itm.api_gateway.config.NatsGatewayProperties;
import com.itm.api_gateway.messaging.NatsRequestClient;
import com.itm.api_gateway.messaging.NatsResponse;
import com.itm.api_gateway.web.checkoutgateway.dto.ProcessCheckoutRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/checkouts")
public class CheckoutGatewayController {

    private final NatsRequestClient natsRequestClient;
    private final NatsGatewayProperties properties;

    public CheckoutGatewayController(NatsRequestClient natsRequestClient, NatsGatewayProperties properties) {
        this.natsRequestClient = natsRequestClient;
        this.properties = properties;
    }

    @PostMapping("/process")
    @Operation(summary = "Procesar checkout", description = "Inicia el proceso de checkout para un carrito cerrado. Valida productos, stock y crea la orden.")
    public ResponseEntity<?> processCheckout(
            @Valid @RequestBody ProcessCheckoutRequest body
    ) {
        NatsResponse response = natsRequestClient.request(
                properties.subject().checkout().process(),
                Map.of("cartId", body.cartId())
        );
        return toHttpResponse(response, HttpStatus.CREATED);
    }

    @PostMapping("/{checkoutId}/confirm")
    @Operation(summary = "Confirmar checkout", description = "Confirma el checkout procesando el pago a través del proveedor de pagos.")
    public ResponseEntity<?> confirmCheckout(
            @Parameter(example = "1") @PathVariable Long checkoutId
    ) {
        NatsResponse response = natsRequestClient.request(
                properties.subject().checkout().confirm(),
                Map.of("checkoutId", checkoutId)
        );
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
        if (lowered.contains("no encontrado") || lowered.contains("no existe")) {
            return HttpStatus.NOT_FOUND;
        }
        if (lowered.contains("obligatoria")
                || lowered.contains("obligatorio")
                || lowered.contains("invalido")
                || lowered.contains("inválido")) {
            return HttpStatus.BAD_REQUEST;
        }
        if (lowered.contains("stock insuficiente")
                || lowered.contains("no ha sido cerrado")
                || lowered.contains("solo se pueden confirmar")) {
            return HttpStatus.CONFLICT;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}