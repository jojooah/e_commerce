package com.jojo.ecommerce.application.exception;

public class OutOfStockException extends RuntimeException {
    public OutOfStockException() {
        super("재고가 부족합니다.");
    }
    public OutOfStockException(String message) {
        super(message);
    }
}