package com.itm.eco_store.cart.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {

    private Long id;
    private String name;
    private Money price;
    private Integer stock;
}
