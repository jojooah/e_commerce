package com.jojo.ecommerce.application.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long paymentId) {
        super("유효하지 않은 결제ID 입니다. paymentId=" + paymentId);
    }
}