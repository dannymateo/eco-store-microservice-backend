package com.itm.eco_store.infrastructure.adapter.in.messaging;

import com.itm.eco_store.application.port.in.CreateProductUseCase;
import com.itm.eco_store.application.port.in.DeleteProductUseCase;
import com.itm.eco_store.application.port.in.GetProductUseCase;
import com.itm.eco_store.application.port.in.UpdateProductUseCase;
import com.itm.eco_store.infrastructure.adapter.in.messaging.dto.NatsCommandResponse;
import com.itm.eco_store.infrastructure.adapter.in.messaging.dto.ProductByIdCommand;
import com.itm.eco_store.infrastructure.adapter.in.messaging.dto.UpdateProductCommand;
import com.itm.eco_store.infrastructure.adapter.in.messaging.dto.CreateProductDTO;
import com.itm.eco_store.infrastructure.adapter.in.messaging.dto.UpdateProductDTO;
import com.itm.eco_store.infrastructure.adapter.in.messaging.mapper.ProductMapper;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class NatsProductCommandAdapter implements InitializingBean, DisposableBean {

    private final NatsProductProperties properties;
    private final ObjectMapper objectMapper;
    private final ProductMapper productMapper;
    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final Connection connection;

    private final Map<String, MessageHandler> handlers = new HashMap<>();
    private Dispatcher dispatcher;

    public NatsProductCommandAdapter(
            NatsProductProperties properties,
            ObjectMapper objectMapper,
            ProductMapper productMapper,
            CreateProductUseCase createProductUseCase,
            GetProductUseCase getProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            DeleteProductUseCase deleteProductUseCase,
            Connection connection
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.productMapper = productMapper;
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.connection = connection;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registerHandlers();
        dispatcher = connection.createDispatcher(this::dispatchMessage);
        handlers.keySet().forEach(dispatcher::subscribe);
    }

    @Override
    public void destroy() throws Exception {
        if (dispatcher != null) {
            dispatcher.unsubscribe(properties.subject().product().create());
            dispatcher.unsubscribe(properties.subject().product().get());
            dispatcher.unsubscribe(properties.subject().product().list());
            dispatcher.unsubscribe(properties.subject().product().update());
            dispatcher.unsubscribe(properties.subject().product().delete());
        }
    }

    private void registerHandlers() {
        handlers.put(properties.subject().product().create(), this::handleCreate);
        handlers.put(properties.subject().product().get(), this::handleGetById);
        handlers.put(properties.subject().product().list(), this::handleGetAll);
        handlers.put(properties.subject().product().update(), this::handleUpdate);
        handlers.put(properties.subject().product().delete(), this::handleDelete);
    }

    private void dispatchMessage(Message message) {
        MessageHandler handler = handlers.get(message.getSubject());
        if (handler == null) {
            return;
        }
        try {
            Object data = handler.handle(message);
            reply(message, NatsCommandResponse.ok(data));
        } catch (Exception ex) {
            reply(message, NatsCommandResponse.error(ex.getMessage()));
        }
    }

    private Object handleCreate(Message message) throws Exception {
        CreateProductDTO command = readCommand(message, CreateProductDTO.class);
        var created = createProductUseCase.create(productMapper.toDomain(command));
        return productMapper.toResponse(created);
    }

    private Object handleGetById(Message message) throws Exception {
        ProductByIdCommand command = readCommand(message, ProductByIdCommand.class);
        Long id = Objects.requireNonNull(command.id(), "id es obligatorio");
        var product = getProductUseCase.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        return productMapper.toResponse(product);
    }

    private Object handleGetAll(Message message) {
        return getProductUseCase.getAll().stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    private Object handleUpdate(Message message) throws Exception {
        UpdateProductCommand command = readCommand(message, UpdateProductCommand.class);
        Long id = Objects.requireNonNull(command.id(), "id es obligatorio");
        UpdateProductDTO dto = new UpdateProductDTO(
                command.name(),
                command.description(),
                command.category(),
                command.originalPrice(),
                command.stock()
        );
        var updated = updateProductUseCase.update(id, productMapper.toDomain(dto));
        return productMapper.toResponse(updated);
    }

    private Object handleDelete(Message message) throws Exception {
        ProductByIdCommand command = readCommand(message, ProductByIdCommand.class);
        Long id = Objects.requireNonNull(command.id(), "id es obligatorio");
        deleteProductUseCase.delete(id);
        return Map.of("deleted", true, "id", id);
    }

    private <T> T readCommand(Message message, Class<T> type) throws Exception {
        byte[] payload = message.getData();
        if (payload == null || payload.length == 0) {
            throw new IllegalArgumentException("El payload del comando es obligatorio");
        }
        return objectMapper.readValue(payload, type);
    }

    private void reply(Message request, NatsCommandResponse response) {
        String replyTo = request.getReplyTo();
        if (replyTo == null || replyTo.isBlank()) {
            return;
        }
        try {
            byte[] responseData = objectMapper.writeValueAsString(response).getBytes(StandardCharsets.UTF_8);
            connection.publish(replyTo, responseData);
        } catch (Exception ignored) {
            // Si no se puede responder, no detenemos el consumidor.
        }
    }

    @FunctionalInterface
    private interface MessageHandler {
        Object handle(Message message) throws Exception;
    }
}
