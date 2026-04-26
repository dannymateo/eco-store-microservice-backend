package com.itm.eco_store.checkout.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutJpaRepository extends JpaRepository<CheckoutEntity, Long> {
}