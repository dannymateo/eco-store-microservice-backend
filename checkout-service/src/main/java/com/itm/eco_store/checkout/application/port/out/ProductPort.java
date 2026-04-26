package com.itm.eco_store.checkout.application.port.out;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductPort {

    Optional<ProductInfo> getProductById(Long productId);

    record ProductInfo(Long id, String name, BigDecimal finalPrice, Integer stock) {}
}