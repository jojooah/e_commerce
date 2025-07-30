package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.domain.model.Point;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointMapper {
    Point selectPointByUserId(Long userId) ;
    Point insertPoint(Point point);
    Point updatePoint(Point point);
}
