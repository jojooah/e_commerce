package com.jojo.ecommerce.application.dto;


import java.util.List;

/**
 * 사용자 요청 객체
 */

public record PaymentRequest(
    Long userId,
    Long couponId,
    Long orderId,
    List<ProductInfo> productInfoList,
    String paymentMethod,
    int paymentPrice
){ }

