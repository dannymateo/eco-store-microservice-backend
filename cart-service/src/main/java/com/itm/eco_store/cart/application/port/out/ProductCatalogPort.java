package com.itm.eco_store.cart.application.port.out;

import com.itm.eco_store.cart.domain.model.ProductInfo;

public interface ProductCatalogPort {

    ProductInfo getProductById(Long productId);
}
