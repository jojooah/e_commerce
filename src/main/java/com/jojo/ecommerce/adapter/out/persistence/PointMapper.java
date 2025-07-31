package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.domain.model.Point;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointMapper {
    /**
     * 포인트 조회
     * @param userId
     * @return
     */
    Point selectPointByUserId(Long userId);

    /**
     * 포인트 충전
     * @param point
     * @return
     */
    int insertPoint(Point point);

    /**
     * 포인트 충전
     * @param point
     * @return
     */
    int updatePoint(Point point);
}
