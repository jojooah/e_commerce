package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.port.out.PointRepositoryPort;
import com.jojo.ecommerce.domain.model.Point;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PointRepositoryMap implements PointRepositoryPort {
    private final Map<Long, Point> repository = new ConcurrentHashMap<>();

    @Override
    public Point updatePoint(Point point) {
        Long userId = point.getUserId();
        repository.get(userId);
        repository.put(userId, point);
        return point;
    }

    @Override
    public Point findPointByUserId(Long userId) {
        if(!repository.containsKey(userId)){
            return new Point(userId,0);
        }

       return repository.get(userId);
    }

}
