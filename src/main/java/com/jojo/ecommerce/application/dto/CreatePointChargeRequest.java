package com.jojo.ecommerce.application.dto;

import com.jojo.ecommerce.domain.model.Point;

public record CreatePointChargeRequest(
        Point point,
        String RequestId

) {
}
