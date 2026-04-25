package com.itm.eco_store.cart.infrastructure.adapter.out.product;

import com.itm.eco_store.cart.application.port.out.ProductCatalogPort;
import com.itm.eco_store.cart.domain.model.Money;
import com.itm.eco_store.cart.domain.model.ProductInfo;
import com.itm.eco_store.cart.infrastructure.adapter.in.messaging.NatsCartProperties;
import io.nats.client.Connection;
import io.nats.client.Message;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Component
public class NatsProductCatalogAdapter implements ProductCatalogPort {

    private final Connection connection;
    private final ObjectMapper objectMapper;
    private final NatsCartProperties properties;

    public NatsProductCatalogAdapter(Connection connection, ObjectMapper objectMapper, NatsCartProperties properties) {
        this.connection = connection;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public ProductInfo getProductById(Long productId) {
        try {
            byte[] request = objectMapper.writeValueAsString(Map.of("id", productId)).getBytes(StandardCharsets.UTF_8);
            Message reply = connection.request(properties.subject().product().get(), request, Duration.ofSeconds(3));
            if (reply == null) {
                throw new IllegalStateException("Sin respuesta del catalogo de productos");
            }

            JsonNode envelope = objectMapper.readTree(reply.getData());
            if (!envelope.path("success").asBoolean(false)) {
                throw new IllegalArgumentException(envelope.path("error").asText("Producto no encontrado"));
            }

            JsonNode data = envelope.path("data");
            return new ProductInfo(
                    data.path("id").asLong(),
                    data.path("name").asText(),
                    Money.of(data.path("finalPrice").decimalValue(), "USD"),
                    data.path("stock").asInt()
            );
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo validar el producto en catalogo", e);
        }
    }
}
