package com.itm.api_gateway.messaging;

import com.itm.api_gateway.config.NatsGatewayProperties;
import io.nats.client.Connection;
import io.nats.client.Message;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class NatsRequestClient {

    private final Connection connection;
    private final ObjectMapper objectMapper;
    private final NatsGatewayProperties properties;

    public NatsRequestClient(Connection connection, ObjectMapper objectMapper, NatsGatewayProperties properties) {
        this.connection = connection;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public NatsResponse request(String subject, Object payload) {
        try {
            byte[] bytes = payload == null
                    ? new byte[0]
                    : objectMapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8);

            Message reply = connection.request(subject, bytes, Duration.ofMillis(properties.requestTimeoutMs()));
            if (reply == null) {
                throw new IllegalStateException("Sin respuesta del microservicio para subject: " + subject);
            }
            return objectMapper.readValue(reply.getData(), NatsResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Fallo en comunicación NATS: " + e.getMessage(), e);
        }
    }
}
