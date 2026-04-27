package com.itm.eco_store.checkout.infrastructure.adapter.out.persistence;

import com.itm.eco_store.checkout.application.port.out.CheckoutRepositoryPort;
import com.itm.eco_store.checkout.domain.model.Checkout;
import com.itm.eco_store.checkout.domain.model.CheckoutItem;
import com.itm.eco_store.checkout.domain.model.CheckoutStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CheckoutRepositoryAdapter implements CheckoutRepositoryPort {

    private final CheckoutJpaRepository jpaRepository;

    public CheckoutRepositoryAdapter(CheckoutJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Checkout save(Checkout checkout) {
        CheckoutEntity entity = toEntity(checkout);
        CheckoutEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Checkout> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    private CheckoutEntity toEntity(Checkout checkout) {
        CheckoutEntity entity = new CheckoutEntity();
        entity.setId(checkout.getId());
        entity.setCartId(checkout.getCartId());
        entity.setStatus(CheckoutEntity.CheckoutStatusEnum.valueOf(checkout.getStatus().name()));
        entity.setTotalAmount(checkout.getTotalAmount());
        entity.setCurrency(checkout.getCurrency());
        entity.setPaymentTransactionId(checkout.getPaymentTransactionId());
        entity.setCreatedAt(checkout.getCreatedAt());
        entity.setConfirmedAt(checkout.getConfirmedAt());

        List<CheckoutItemEntity> itemEntities = checkout.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .toList();
        entity.setItems(itemEntities);

        return entity;
    }

    private CheckoutItemEntity toItemEntity(CheckoutItem item, CheckoutEntity checkoutEntity) {
        CheckoutItemEntity entity = new CheckoutItemEntity();
        entity.setCheckout(checkoutEntity);
        entity.setProductId(item.getProductId());
        entity.setProductName(item.getProductName());
        entity.setUnitPrice(item.getUnitPrice());
        entity.setQuantity(item.getQuantity());
        entity.setLineTotal(item.getLineTotal());
        return entity;
    }

    private Checkout toDomain(CheckoutEntity entity) {
        List<CheckoutItem> items = entity.getItems().stream()
                .map(this::toItemDomain)
                .toList();

        return new Checkout(
                entity.getId(),
                entity.getCartId(),
                CheckoutStatus.valueOf(entity.getStatus().name()),
                items,
                entity.getTotalAmount(),
                entity.getCurrency(),
                entity.getPaymentTransactionId(),
                entity.getCreatedAt(),
                entity.getConfirmedAt()
        );
    }

    private CheckoutItem toItemDomain(CheckoutItemEntity entity) {
        return new CheckoutItem(
                entity.getProductId(),
                entity.getProductName(),
                entity.getUnitPrice(),
                entity.getQuantity(),
                entity.getLineTotal()
        );
    }
}