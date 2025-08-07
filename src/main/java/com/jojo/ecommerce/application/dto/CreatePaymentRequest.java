package com.jojo.ecommerce.application.dto;


import java.util.List;

/**
 * 사용자 요청 객체
 */

public record CreatePaymentRequest(
    Long userId,
    Long couponId,
    Long orderId,
    List<ProductDto> productDtoList,
    String paymentMethod,
    int paymentPrice
){ }

