package com.jojo.ecommerce.application.exception;

public class AlreadyCompletedOrder extends RuntimeException {
    public AlreadyCompletedOrder() {
        super("이미 완료된 주문입니다");
    }
}
