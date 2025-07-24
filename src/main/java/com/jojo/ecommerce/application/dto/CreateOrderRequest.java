package com.jojo.ecommerce.application.dto;

import lombok.Getter;

import java.util.List;

/**
 * 사용자 요청 객체
 */
public class CreateOrderRequest {
    @Getter
    private Long userId;
    @Getter
    private Long couponId;
    @Getter
    private int totalQuantity;
    @Getter
    private int totalPrice;
    @Getter
    private List<ProductDto> productDtoList;

    public CreateOrderRequest(Long userId, Long couponId, int totalQuantity, int totalPrice, List<ProductDto> productDtoList) {
        this.userId = userId;
        this.couponId = couponId;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.productDtoList = productDtoList;
    }

}

