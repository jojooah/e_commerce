package com.jojo.ecommerce.application.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("유효하지 않은 상품ID 입니다. productId=" + productId);
    }
}