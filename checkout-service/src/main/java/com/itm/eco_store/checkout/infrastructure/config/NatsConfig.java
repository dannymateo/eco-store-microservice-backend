package com.itm.eco_store.checkout.infrastructure.config;

import com.itm.eco_store.checkout.infrastructure.adapter.in.messaging.NatsCheckoutProperties;
import io.nats.client.Connection;
import io.nats.client.Nats;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(NatsCheckoutProperties.class)
public class NatsConfig {

    @Bean
    public Connection natsConnection(NatsCheckoutProperties properties) throws IOException, InterruptedException {
        return Nats.connect(properties.url());
    }
}