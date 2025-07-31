package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.domain.Common;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItem extends Common {
    private Long orderItemId;
    private Long orderId;
    private Long productId;
    private int unitPrice;
    private STATUS_TYPE paymentStatus = STATUS_TYPE.PAYMENT_PENDING;
    private int quantity;           //수량

    public OrderItem(Long productId, int quantity, int unitPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice=unitPrice;
    }

    public OrderItem(Long orderId, Long productId, int quantity, int unitPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice=unitPrice;
    }

}
