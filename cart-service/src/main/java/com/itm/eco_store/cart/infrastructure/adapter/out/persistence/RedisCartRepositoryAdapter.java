package com.itm.eco_store.cart.infrastructure.adapter.out.persistence;

import com.itm.eco_store.cart.application.port.out.CartRepositoryPort;
import com.itm.eco_store.cart.domain.model.Cart;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Component
public class RedisCartRepositoryAdapter implements CartRepositoryPort {

    private static final String KEY_PREFIX = "cart:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCartRepositoryAdapter(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Cart save(Cart cart, Duration ttl) {
        try {
            String key = toKey(cart.getId());
            String payload = objectMapper.writeValueAsString(cart);
            if (ttl == null) {
                redisTemplate.opsForValue().set(key, payload);
                return cart;
            }
            redisTemplate.opsForValue().set(key, payload, ttl);
            return cart;
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo persistir el carrito en Redis", e);
        }
    }

    @Override
    public Optional<Cart> findById(String cartId) {
        try {
            String key = toKey(cartId);
            String value = redisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(value, Cart.class));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo recuperar el carrito desde Redis", e);
        }
    }

    private String toKey(String cartId) {
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException("cartId es obligatorio");
        }
        return KEY_PREFIX + cartId.trim();
    }
}
