package com.itm.eco_store.checkout.infrastructure.adapter.in.messaging;

import com.itm.eco_store.checkout.application.port.in.IConfirmCheckoutUseCase;
import com.itm.eco_store.checkout.application.port.in.IProcessCheckoutUseCase;
import com.itm.eco_store.checkout.infrastructure.adapter.in.messaging.dto.ConfirmCheckoutCommand;
import com.itm.eco_store.checkout.infrastructure.adapter.in.messaging.dto.NatsCommandResponse;
import com.itm.eco_store.checkout.infrastructure.adapter.in.messaging.dto.ProcessCheckoutCommand;
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

@Component
public class CheckoutController implements InitializingBean, DisposableBean {

    private final NatsCheckoutProperties properties;
    private final ObjectMapper objectMapper;
    private final Connection connection;
    private final IProcessCheckoutUseCase processCheckoutUseCase;
    private final IConfirmCheckoutUseCase confirmCheckoutUseCase;

    private final Map<String, MessageHandler> handlers = new HashMap<>();
    private Dispatcher dispatcher;

    public CheckoutController(
            NatsCheckoutProperties properties,
            ObjectMapper objectMapper,
            Connection connection,
            IProcessCheckoutUseCase processCheckoutUseCase,
            IConfirmCheckoutUseCase confirmCheckoutUseCase
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.connection = connection;
        this.processCheckoutUseCase = processCheckoutUseCase;
        this.confirmCheckoutUseCase = confirmCheckoutUseCase;
    }

    @Override
    public void afterPropertiesSet() {
        registerHandlers();
        dispatcher = connection.createDispatcher(this::dispatchMessage);
        handlers.keySet().forEach(dispatcher::subscribe);
    }

    @Override
    public void destroy() {
        if (dispatcher != null) {
            dispatcher.unsubscribe(properties.subject().checkout().process());
            dispatcher.unsubscribe(properties.subject().checkout().confirm());
        }
    }

    private void registerHandlers() {
        handlers.put(properties.subject().checkout().process(), this::handleProcessCheckout);
        handlers.put(properties.subject().checkout().confirm(), this::handleConfirmCheckout);
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

    private Object handleProcessCheckout(Message message) throws Exception {
        ProcessCheckoutCommand command = readCommand(message, ProcessCheckoutCommand.class);
        String cartId = requireCartId(command.cartId());
        return processCheckoutUseCase.processCheckout(cartId);
    }

    private Object handleConfirmCheckout(Message message) throws Exception {
        ConfirmCheckoutCommand command = readCommand(message, ConfirmCheckoutCommand.class);
        Long checkoutId = Objects.requireNonNull(command.checkoutId(), "checkoutId es obligatorio");
        return confirmCheckoutUseCase.confirmCheckout(checkoutId);
    }

    private <T> T readCommand(Message message, Class<T> type) throws Exception {
        byte[] payload = message.getData();
        if (payload == null || payload.length == 0) {
            throw new IllegalArgumentException("El payload del comando es obligatorio");
        }
        return objectMapper.readValue(payload, type);
    }

    private String requireCartId(String cartId) {
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException("cartId es obligatorio");
        }
        return cartId.trim();
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