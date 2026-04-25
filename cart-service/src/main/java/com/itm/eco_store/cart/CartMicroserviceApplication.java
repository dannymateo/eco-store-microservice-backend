package com.itm.eco_store.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CartMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartMicroserviceApplication.class, args);
    }
}
