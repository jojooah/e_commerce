package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.domain.Common;
import lombok.Setter;

public class OrderItem extends Common {
    private Long orderItemId;
    private Long orderId;
    private Long productId;
    @Setter
    private STATUS_TYPE paymentStatus = STATUS_TYPE.PAYMENT_PENDING;
    private int quantity;           //수량

    public OrderItem(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderItem(Long orderId, Long productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public void paymentCompleted() {
         this.paymentStatus = STATUS_TYPE.PAYMENT_COMPLETED;
    }

    public void paymentCanceled() {
        this.paymentStatus = STATUS_TYPE.PAYMENT_CANCELED;
    }

}
