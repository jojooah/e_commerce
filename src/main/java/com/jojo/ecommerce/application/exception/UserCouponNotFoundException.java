package com.jojo.ecommerce.application.exception;

public class UserCouponNotFoundException extends RuntimeException {
    public UserCouponNotFoundException(Long couponId,Long userId) {
        super("유효하지 않은 사용자-쿠폰 정보입니다. userId=" + userId + ", couponId=" + couponId);
    }
}