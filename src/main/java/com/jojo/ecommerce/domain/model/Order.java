package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Order extends Common {
    @Getter
    @Setter
    private Long orderId;
    @Getter
    private Long userId;
    @Setter
    @Getter
    private STATUS_TYPE paymentStatus = STATUS_TYPE.PAYMENT_PENDING;
    @Getter
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    public Order(Long userId) {
        this.userId = userId;
    }

    public void paymentCompleted() {
        this.paymentStatus = STATUS_TYPE.PAYMENT_COMPLETED;
    }

    public void paymentCanceled() {
        this.paymentStatus = STATUS_TYPE.PAYMENT_CANCELED;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }


}
