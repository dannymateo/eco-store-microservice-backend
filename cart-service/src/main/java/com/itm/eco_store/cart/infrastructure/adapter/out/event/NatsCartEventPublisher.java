package com.itm.eco_store.cart.infrastructure.adapter.out.event;

import com.itm.eco_store.cart.application.port.out.CartEventPort;
import com.itm.eco_store.cart.domain.model.Cart;
import com.itm.eco_store.cart.infrastructure.adapter.in.messaging.NatsCartProperties;
import io.nats.client.Connection;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class NatsCartEventPublisher implements CartEventPort {

    private final Connection connection;
    private final ObjectMapper objectMapper;
    private final NatsCartProperties properties;

    public NatsCartEventPublisher(Connection connection, ObjectMapper objectMapper, NatsCartProperties properties) {
        this.connection = connection;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public void publishCartCheckedOut(Cart cart) {
        try {
            Map<String, Object> event = Map.of(
                    "eventType", "CART_CHECKED_OUT",
                    "cartId", cart.getId(),
                    "checkedOut", cart.isCheckedOut(),
                    "items", cart.getItems(),
                    "total", cart.total()
            );
            byte[] payload = objectMapper.writeValueAsString(event).getBytes(StandardCharsets.UTF_8);
            connection.publish(properties.subject().cart().checkoutEvent(), payload);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo publicar evento de checkout", e);
        }
    }
}
