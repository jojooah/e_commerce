package com.jojo.ecommerce.application.dto;

import com.jojo.ecommerce.domain.Common;
import com.jojo.ecommerce.domain.model.Product;
import lombok.Getter;

public class ProductDto extends Common {
    @Getter
    Long productId;                 // id
    @Getter
    String productName;             // 상품명
    @Getter
    int qauntity;                   // 개수
    @Getter
    int productPrice;               // 가격
    @Getter
    int productCode;                // 상품코드

    public ProductDto(Long productId, String productName, int qauntity, int price, int productCode) {
        this.productId = productId;
        this.productName = productName;
        this.qauntity = qauntity;
        this.productPrice = price;
        this.productCode = productCode;
    }

   public static ProductDto of(Product product){
       return new ProductDto(product.getProductId(), product.getProductName(), product.getStock(), product.getProductPrice(), product.getProductCode());
   }
}
