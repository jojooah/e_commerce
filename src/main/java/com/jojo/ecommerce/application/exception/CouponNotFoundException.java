package com.jojo.ecommerce.application.exception;

public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(Long couponId) {
        super("유효하지 않은 쿠폰ID 입니다. couponId=" + couponId);
    }
}