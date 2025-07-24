package com.jojo.ecommerce.application.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long orderId) {
        super("유효하지 않은 주문ID 입니다. orderId=" + orderId);
    }
}