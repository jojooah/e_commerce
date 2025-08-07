package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class Order extends Common {
    private Long orderId;
    private Long userId;
    private STATUS_TYPE paymentStatus = STATUS_TYPE.PAYMENT_PENDING;
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    public Order(Long userId) {
        this.userId = userId;
    }

    public void paymentCompleted() {
        this.paymentStatus = STATUS_TYPE.PAYMENT_COMPLETED;

        for(OrderItem orderItem : orderItems) {
            orderItem.setPaymentStatus(STATUS_TYPE.PAYMENT_COMPLETED);
        }
    }

    public void paymentCanceled() {
        this.paymentStatus = STATUS_TYPE.PAYMENT_CANCELED;
    }

    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
    }

    public int calculateTotalPrice() {
        return orderItems.stream()
                .mapToInt(item -> item.getUnitPrice() * item.getQuantity())
                .sum();
    }

}
