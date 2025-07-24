package com.jojo.ecommerce.application.exception;

public class OutOfCouponException extends RuntimeException {
    public OutOfCouponException() {
        super("더이상 발급 가능한 쿠폰이 없습니다.");
    }
    public OutOfCouponException(Long campaignId) {
        super("더이상 발급 가능한 쿠폰이 없습니다. campaignId=" + campaignId);
    }
}