package com.jojo.ecommerce.domain.model;


import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.Setter;

public class Product extends Common {
    @Getter
    @Setter
    long productId;                 // id
    @Getter
    String productName;             // 상품명
    @Getter
    int stock;                      // 재고
    @Getter
    int productPrice;               // 가격
    @Getter
    int productCode;                // 상품코드

    public Product(String productName, int stock, int productPrice, int productCode) {
        this.productName = productName;
        this.stock = stock;
        this.productPrice = productPrice;
        this.productCode = productCode;

    }

    // 재고 차감
    public void decreaseStock(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다. 요청된 수량: " + qty);
        }
        if (this.stock < qty) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + this.stock + ", 요청 수량: " + qty);
        }
        this.stock -= qty;
    }


}
