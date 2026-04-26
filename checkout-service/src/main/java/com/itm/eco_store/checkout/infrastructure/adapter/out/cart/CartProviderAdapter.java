package com.itm.eco_store.checkout.infrastructure.adapter.out.cart;

import com.itm.eco_store.checkout.application.port.out.CartPort;
import com.itm.eco_store.checkout.infrastructure.adapter.in.messaging.NatsCheckoutProperties;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.nats.client.Message;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CartProviderAdapter implements CartPort {

    private final Connection connection;
    private final ObjectMapper objectMapper;
    private final NatsCheckoutProperties properties;

    public CartProviderAdapter(Connection connection, ObjectMapper objectMapper, NatsCheckoutProperties properties) {
        this.connection = connection;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public Optional<CartInfo> getCartById(String cartId) {
        try {
            byte[] payload = objectMapper.writeValueAsString(java.util.Map.of("cartId", cartId))
                    .getBytes(java.nio.charset.StandardCharsets.UTF_8);
            Message reply = connection.request(
                    properties.subject().cart().get(),
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
            List<CartItemInfo> items = new ArrayList<>();
            JsonNode itemsNode = data.get("items");
            if (itemsNode != null && itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    items.add(new CartItemInfo(
                            itemNode.get("product").get("id").asLong(),
                            itemNode.get("quantity").asInt()
                    ));
                }
            }
            return Optional.of(new CartInfo(
                    data.get("id").asText(),
                    data.get("checkedOut").asBoolean(),
                    items
            ));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}