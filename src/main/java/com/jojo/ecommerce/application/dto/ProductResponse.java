package com.jojo.ecommerce.application.dto;

import com.jojo.ecommerce.domain.model.Product;

// 응답 DTO (record)
public record ProductResponse(
        Long productId,
        String productName,
        int productPrice,
        int productCode
) {
    public static ProductResponse of(Product p) {
        return new ProductResponse(
                p.getProductId(),
                p.getProductName(),
                p.getProductPrice(),
                p.getProductCode()
        );
    }
}
