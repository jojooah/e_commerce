package com.jojo.ecommerce.application.dto;

import com.jojo.ecommerce.domain.model.OrderItem;

import java.time.Instant;
import java.util.List;

public record OrderPlaceEvent(
        Long orderId,
        Long userId,
        String requestId,
        int totalQuantity,
        int totalAmount,
        List<OrderItem> items,
        Instant occurredAt
) {
}
