package com.jojo.ecommerce.domain.model;


import com.jojo.ecommerce.application.exception.OutOfStockException;
import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Product extends Common {
    Long productId;                 // id
    String productName;             // 상품명
    int stock;                      // 재고
    int productPrice;               // 가격
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
            throw new OutOfStockException("재고가 부족합니다. 현재 재고: " + this.stock + ", 요청 수량: " + qty);
        }
        this.stock -= qty;
    }

    // 재고 원복
    public void restoreStock(int qty) {
        this.stock += qty;
    }


}
