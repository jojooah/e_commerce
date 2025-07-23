package com.jojo.ecommerce.application.dto;

import com.jojo.ecommerce.domain.Common;
import com.jojo.ecommerce.domain.model.Product;
import lombok.Getter;

public class ProductDto extends Common {
    @Getter
    long productId;                 // id
    @Getter
    String productName;             // 상품명
    @Getter
    int stock;                      // 재고
    @Getter
    int productPrice;               // 가격
    @Getter
    int productCode;                // 상품코드

    public ProductDto(long productId, String productName, int stock, int productPrice, int productCode) {
        this.productId = productId;
        this.productName = productName;
    }

   public static ProductDto of(Product product){
       return new ProductDto(product.getProductId(), product.getProductName(), product.getStock(), product.getProductPrice(), product.getProductCode());
   }
}
