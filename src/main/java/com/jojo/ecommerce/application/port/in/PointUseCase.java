package com.jojo.ecommerce.application.port.in;

import com.jojo.ecommerce.domain.model.Point;

public interface PointUseCase {
    // 포인트 충전
    Point chargePoint(Point point);

}
