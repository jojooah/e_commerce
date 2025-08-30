package com.jojo.ecommerce.application.dto;

import com.jojo.ecommerce.domain.model.OrderItem;

import java.time.Instant;
import java.util.List;

public record PaymentCompleteEvent(
        Long orderId,
        Long paymentId,
        Long userId,
        String requestId,
        int paidAmount,
        List<OrderItem> items,
        Instant occurredAt
) {
}
