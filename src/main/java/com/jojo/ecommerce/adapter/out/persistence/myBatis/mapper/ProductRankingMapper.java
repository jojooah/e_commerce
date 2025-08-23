package com.jojo.ecommerce.adapter.out.persistence.myBatis.mapper;

import com.jojo.ecommerce.application.dto.ProductRankResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProductRankingMapper {
    List<ProductRankResponse> selectTopSoldProducts(@Param("limit") int limit,
                                                    @Param("from") LocalDateTime from,
                                                    @Param("to") LocalDateTime to);
}
