package com.itm.eco_store.checkout.infrastructure.adapter.out.product;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.itm.eco_store.checkout.application.port.out.ProductPort;
import com.itm.eco_store.checkout.infrastructure.adapter.in.messaging.NatsCheckoutProperties;

import io.nats.client.Connection;
import io.nats.client.Message;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProductProviderAdapter implements ProductPort {

    private final Connection connection;
    private final ObjectMapper objectMapper;
    private final NatsCheckoutProperties properties;

    public ProductProviderAdapter(Connection connection, ObjectMapper objectMapper, NatsCheckoutProperties properties) {
        this.connection = connection;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public Optional<ProductInfo> getProductById(Long productId) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(java.util.Map.of("id", productId));
            Message reply = connection.request(
                    properties.subject().product().get(),
                    payload,
                    Duration.ofSeconds(3)
            );
            if (reply == null) {
                return Optional.empty();
            }
            JsonNode response = objectMapper.readTree(reply.getData());
            if (!response.has("success") || !response.get("success").asBoolean()) {
                return Optional.empty();
            }
            JsonNode data = response.get("data");
            return Optional.of(new ProductInfo(
                    data.get("id").asLong(),
                    data.get("name").asText(),
                    new BigDecimal(data.get("finalPrice").asText()),
                    data.has("stock") ? data.get("stock").asInt() : 0
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}