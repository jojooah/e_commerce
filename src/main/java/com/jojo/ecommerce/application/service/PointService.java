package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.port.in.PointUseCase;
import com.jojo.ecommerce.application.port.out.PointRepositoryPort;
import com.jojo.ecommerce.domain.model.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService implements PointUseCase {
    private final PointRepositoryPort pointRepositoryPort;


    @Override
    public Point chargePoint(Point point) {
        Point savedPoint = pointRepositoryPort.findPointByUserId(point.getUserId());
        Point chargedPoint = savedPoint.addPoint(point.getPoint());
        return pointRepositoryPort.updatePoint(chargedPoint);
    }

}
