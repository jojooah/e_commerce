package com.jojo.ecommerce.application.exception;

public class AlreadyIssuedException extends RuntimeException {
    public AlreadyIssuedException(Long userId, Long campaignId) {
        super("사용자 " + userId + "는 이미 해당 캠페인 쿠폰 지급됨 " + campaignId);
    }
}
