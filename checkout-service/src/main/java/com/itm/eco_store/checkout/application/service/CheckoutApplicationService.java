package com.itm.eco_store.checkout.application.service;

import com.itm.eco_store.checkout.application.port.in.IConfirmCheckoutUseCase;
import com.itm.eco_store.checkout.application.port.in.IProcessCheckoutUseCase;
import com.itm.eco_store.checkout.application.port.out.CartPort;
import com.itm.eco_store.checkout.application.port.out.CartPort.CartInfo;
import com.itm.eco_store.checkout.application.port.out.CartPort.CartItemInfo;
import com.itm.eco_store.checkout.application.port.out.CheckoutRepositoryPort;
import com.itm.eco_store.checkout.application.port.out.PaymentPort;
import com.itm.eco_store.checkout.application.port.out.PaymentPort.PaymentResult;
import com.itm.eco_store.checkout.application.port.out.ProductPort;
import com.itm.eco_store.checkout.application.port.out.ProductPort.ProductInfo;
import com.itm.eco_store.checkout.domain.model.Checkout;
import com.itm.eco_store.checkout.domain.model.CheckoutItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService implements IProcessCheckoutUseCase, IConfirmCheckoutUseCase {

    private final CheckoutRepositoryPort checkoutRepositoryPort;
    private final CartPort cartPort;
    private final ProductPort productPort;
    private final PaymentPort paymentPort;

    @Override
    @Transactional
    public Checkout processCheckout(String cartId) {
        CartInfo cart = cartPort.getCartById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado: " + cartId));

        if (!cart.checkedOut()) {
            throw new IllegalStateException("El carrito no ha sido cerrado. Debe cerrar el carrito antes del checkout.");
        }

        List<CheckoutItem> checkoutItems = new ArrayList<>();
        for (CartItemInfo cartItem : cart.items()) {
            ProductInfo product = productPort.getProductById(cartItem.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + cartItem.productId()));

            if (product.stock() < cartItem.quantity()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + product.name());
            }

            checkoutItems.add(CheckoutItem.of(
                    product.id(),
                    product.name(),
                    product.finalPrice(),
                    cartItem.quantity()
            ));
        }

        Checkout checkout = Checkout.create(cartId, checkoutItems);
        return checkoutRepositoryPort.save(checkout);
    }

    @Override
    @Transactional
    public Checkout confirmCheckout(Long checkoutId) {
        Checkout checkout = checkoutRepositoryPort.findById(checkoutId)
                .orElseThrow(() -> new IllegalArgumentException("Checkout no encontrado: " + checkoutId));

        PaymentResult result = paymentPort.processPayment(
                checkout.getId(),
                checkout.getTotalAmount(),
                checkout.getCurrency()
        );

        if (result.success()) {
            checkout.confirm(result.transactionId());
        } else {
            checkout.markAsFailed();
        }

        return checkoutRepositoryPort.save(checkout);
    }
}