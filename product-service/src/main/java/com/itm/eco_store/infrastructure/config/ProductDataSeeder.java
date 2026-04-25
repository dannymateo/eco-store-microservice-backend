package com.itm.eco_store.infrastructure.config;

import com.itm.eco_store.application.port.out.ProductRepositoryPort;
import com.itm.eco_store.domain.model.Category;
import com.itm.eco_store.domain.model.Product;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@ConditionalOnProperty(value = "app.seed.products.enabled", havingValue = "true", matchIfMissing = true)
public class ProductDataSeeder implements ApplicationRunner {

    private final ProductRepositoryPort repository;

    public ProductDataSeeder(ProductRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Product> defaults = List.of(
                Product.create(
                        "Camiseta Eco Bamboo",
                        "Camiseta de fibra de bamboo talla M",
                        Category.NORMAL,
                        new BigDecimal("79.90"),
                        35
                ),
                Product.create(
                        "Pantalon Recycled Denim",
                        "Pantalon elaborado con denim reciclado",
                        Category.NORMAL,
                        new BigDecimal("129.00"),
                        20
                ),
                Product.create(
                        "Chaqueta Green Season",
                        "Chaqueta de temporada pasada con descuento",
                        Category.TEMPORADA_PASADA,
                        new BigDecimal("199.00"),
                        12
                ),
                Product.create(
                        "Tenis Urban Eco",
                        "Tenis unisex fabricados con materiales sostenibles",
                        Category.TEMPORADA_PASADA,
                        new BigDecimal("159.50"),
                        18
                )
        );

        defaults.stream()
                .filter(product -> !repository.existsByName(product.getName()))
                .forEach(repository::save);
    }
}
