package com.itm.eco_store.users.infrastructure.adapter.in.messaging;

import com.itm.eco_store.users.application.service.AuthApplicationService;
import com.itm.eco_store.users.application.service.UserApplicationService;
import com.itm.eco_store.users.domain.model.UserInfo;
import com.itm.eco_store.users.domain.model.User;
import com.itm.eco_store.users.domain.ports.in.IAuthUseCase;
import com.itm.eco_store.users.domain.ports.in.IUserManagementUseCase;
import com.itm.eco_store.users.domain.service.TokenService;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class NatsUserCommandAdapter implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(NatsUserCommandAdapter.class);

    private final NatsUserProperties properties;
    private final ObjectMapper objectMapper;
    private final AuthApplicationService authUseCase;
    private final UserApplicationService userUseCase;
    private final TokenService tokenService;
    private final Connection connection;

    private final Map<String, MessageHandler> handlers = new HashMap<>();
    private Dispatcher dispatcher;

    public NatsUserCommandAdapter(
            NatsUserProperties properties,
            ObjectMapper objectMapper,
            AuthApplicationService authUseCase,
            UserApplicationService userUseCase,
            TokenService tokenService,
            Connection connection
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.authUseCase = authUseCase;
        this.userUseCase = userUseCase;
        this.tokenService = tokenService;
        this.connection = connection;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        registerHandlers();
        dispatcher = connection.createDispatcher(this::dispatchMessage);
        handlers.keySet().forEach(dispatcher::subscribe);
        logger.info("[NATS-USER] Listening on subjects: {}", handlers.keySet());
    }

    @Override
    public void destroy() throws Exception {
        if (dispatcher != null) {
            handlers.keySet().forEach(dispatcher::unsubscribe);
        }
    }

    private void registerHandlers() {
        handlers.put(properties.subject().auth().login(), this::handleLogin);
        handlers.put(properties.subject().auth().register(), this::handleRegister);
        handlers.put(properties.subject().auth().forgotPassword(), this::handleForgotPassword);
        handlers.put(properties.subject().auth().resetPassword(), this::handleResetPassword);
        handlers.put(properties.subject().user().create(), this::handleCreateUser);
        handlers.put(properties.subject().user().get(), this::handleGetUser);
        handlers.put(properties.subject().user().update(), this::handleUpdateUser);
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
            logger.error("[NATS-USER] Error handling {}: {}", message.getSubject(), ex.getMessage());
            reply(message, NatsCommandResponse.error(ex.getMessage()));
        }
    }

    private Object handleLogin(Message message) throws Exception {
        Map<String, Object> command = readCommand(message);
        String email = (String) command.get("email");
        String password = (String) command.get("password");

        IAuthUseCase.AuthResponse response = authUseCase.login(new IAuthUseCase.LoginCommand(email, password));
        Map<String, Object> result = new HashMap<>();
        result.put("token", response.token());
        result.put("userInfo", Map.of(
                "id", response.userInfo().id(),
                "email", response.userInfo().email(),
                "role", response.userInfo().role()
        ));
        return result;
    }

    private Object handleRegister(Message message) throws Exception {
        Map<String, Object> command = readCommand(message);
        String email = (String) command.get("email");
        String password = (String) command.get("password");
        String role = (String) command.get("role");

        UserInfo userInfo = authUseCase.register(new IAuthUseCase.RegisterCommand(email, password, role));
        return Map.of(
                "id", userInfo.id(),
                "email", userInfo.email(),
                "role", userInfo.role()
        );
    }

    private Object handleForgotPassword(Message message) throws Exception {
        Map<String, Object> command = readCommand(message);
        String email = (String) command.get("email");

        var userOpt = userUseCase.getUserByEmail(email);
        if (userOpt.isEmpty()) {
            return Map.of("message", "Se ha enviado un email para restablecer la contraseña");
        }

        User user = userOpt.get();
        String resetToken = tokenService.generateResetToken(user);

        return Map.of(
                "message", "Se ha enviado un email para restablecer la contraseña",
                "resetToken", resetToken
        );
    }

    private Object handleResetPassword(Message message) throws Exception {
        Map<String, Object> command = readCommand(message);
        String token = (String) command.get("token");
        String newPassword = (String) command.get("newPassword");

        if (token == null || newPassword == null) {
            throw new IllegalArgumentException("El token y la nueva contraseña son obligatorios");
        }

        if (!tokenService.validateResetToken(token)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        Long userId = tokenService.getUserIdFromToken(token);
        UserInfo userInfo = userUseCase.getUser(new IUserManagementUseCase.GetUserQuery(userId));

        userUseCase.resetPassword(userId, newPassword);

        return Map.of("message", "Contraseña actualizada correctamente");
    }

    private Object handleCreateUser(Message message) throws Exception {
        Map<String, Object> command = readCommand(message);
        String email = (String) command.get("email");
        String password = (String) command.get("password");
        String role = (String) command.get("role");

        UserInfo userInfo = userUseCase.createUser(new IUserManagementUseCase.CreateUserCommand(email, password, role));
        return Map.of(
                "id", userInfo.id(),
                "email", userInfo.email(),
                "role", userInfo.role()
        );
    }

    private Object handleGetUser(Message message) throws Exception {
        Map<String, Object> command = readCommand(message);
        Long id = ((Number) command.get("id")).longValue();

        UserInfo userInfo = userUseCase.getUser(new IUserManagementUseCase.GetUserQuery(id));
        return Map.of(
                "id", userInfo.id(),
                "email", userInfo.email(),
                "role", userInfo.role()
        );
    }

    private Object handleUpdateUser(Message message) throws Exception {
        Map<String, Object> command = readCommand(message);
        Long id = ((Number) command.get("id")).longValue();
        String email = (String) command.get("email");
        String role = (String) command.get("role");
        Boolean active = (Boolean) command.get("active");

        UserInfo userInfo = userUseCase.updateUser(new IUserManagementUseCase.UpdateUserCommand(id, email, role, active));
        return Map.of(
                "id", userInfo.id(),
                "email", userInfo.email(),
                "role", userInfo.role()
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readCommand(Message message) throws Exception {
        byte[] payload = message.getData();
        if (payload == null || payload.length == 0) {
            throw new IllegalArgumentException("El payload del comando es obligatorio");
        }
        return objectMapper.readValue(payload, Map.class);
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
            logger.warn("[NATS-USER] Could not reply to {}", replyTo);
        }
    }

    @FunctionalInterface
    private interface MessageHandler {
        Object handle(Message message) throws Exception;
    }
}