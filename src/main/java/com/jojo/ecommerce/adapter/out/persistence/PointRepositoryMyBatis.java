package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.port.out.PointRepositoryPort;
import com.jojo.ecommerce.domain.model.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
@RequiredArgsConstructor
public class PointRepositoryMyBatis implements PointRepositoryPort {
    private final PointMapper pointMapper;

    @Override
    public Point updatePoint(Point point) {
        Point savedPoint = pointMapper.selectPointByUserId(point.getUserId());

        if (ObjectUtils.isEmpty(savedPoint)) {
            return pointMapper.insertPoint(point);
        }

        return pointMapper.updatePoint(point);

    }

    @Override
    public Point findPointByUserId(Long userId) {
        Point point = pointMapper.selectPointByUserId(userId);
        if (ObjectUtils.isEmpty(point)) {
            return new Point(userId, 0);
        }

        return point;
    }
}
