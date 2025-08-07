package com.jojo.ecommerce.application.dto;


import java.util.List;

/**
 * 사용자 요청 객체
 */
public record CreateOrderRequest (
     Long userId,
     Long couponId,
     int totalQuantity,
     int totalPrice,
     List<ProductDto> productDtoList
){ }

