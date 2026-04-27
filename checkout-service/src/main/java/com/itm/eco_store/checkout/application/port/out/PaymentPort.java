package com.itm.eco_store.checkout.application.port.out;

import java.math.BigDecimal;

public interface PaymentPort {

    PaymentResult processPayment(Long checkoutId, BigDecimal amount, String currency);

    record PaymentResult(boolean success, String transactionId, String message) {
        public static PaymentResult ok(String transactionId) {
            return new PaymentResult(true, transactionId, "Pago procesado exitosamente");
        }

        public static PaymentResult fail(String message) {
            return new PaymentResult(false, null, message);
        }
    }
}