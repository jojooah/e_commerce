package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.port.out.PointRepositoryPort;
import com.jojo.ecommerce.domain.model.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

/**
 * myBatis어답터 구현
 */
@Primary // 이거 붙여줘야 이 어답터를 사용.
@Repository
@RequiredArgsConstructor
public class PointRepositoryMyBatis implements PointRepositoryPort {
    private final PointMapper pointMapper;

    @Override
    public Point updatePoint(Point point) {
        Point savedPoint = pointMapper.selectPointByUserId(point.getUserId());

        if (ObjectUtils.isEmpty(savedPoint)) {
             pointMapper.insertPoint(point);
             return point;
        }

         pointMapper.updatePoint(point);
        return point;

    }

    @Override
    public Point findPointByUserId(Long userId) {
        return pointMapper.selectPointByUserId(userId);
    }

    @Override
    public void saveOrUpdatePoint(Point point) {
        pointMapper.upsertAndAddPoint(point);
    }

    @Override
    public void savePointCharge(Long userId,String requestId,int amount){
        Point point = new Point(userId,amount);
        point.setRequestId(requestId);

        pointMapper.insertPointCharge(point);
    }
}
