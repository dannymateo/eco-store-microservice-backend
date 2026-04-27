package com.itm.eco_store.checkout.infrastructure.adapter.out.payment;

import com.itm.eco_store.checkout.application.port.out.PaymentPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentProviderAdapter implements PaymentPort {

    private static final Logger log = LoggerFactory.getLogger(PaymentProviderAdapter.class);

    @Override
    public PaymentResult processPayment(Long checkoutId, BigDecimal amount, String currency) {
        // Mocked external payment provider call
        // Simulates calling an external payment gateway (e.g., Stripe, PayPal)
        try {
            log.info("[MOCK PAYMENT] Procesando pago para checkoutId={}, amount={} {}, simulando llamada a proveedor externo...",
                    checkoutId, amount, currency);

            // Simulate network latency (200-500ms)
            Thread.sleep(200 + (long) (Math.random() * 300));

            // Generate mock transaction ID
            String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            log.info("[MOCK PAYMENT] Pago exitoso. transactionId={}", transactionId);
            return PaymentResult.ok(transactionId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentResult.fail("Pago interrumpido");
        }
    }
}