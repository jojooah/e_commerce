package com.jojo.ecommerce.application.dto;


import java.util.List;

/**
 * 사용자 요청 객체
 */
public record ProductOrderRequest (
     Long userId,
     Long couponId,
     String requestId,
     int totalQuantity,
     int totalPrice,
     List<ProductInfo> productDtoList
){ }

