package com.jojo.ecommerce.application.dto;

public record ProductInfo (
    Long productId,               // id
    String productName,           // 상품명
    int quantity,                  // 개수
    int productPrice,             // 가격
    int productCode              // 상품코드
) {}
