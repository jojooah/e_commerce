package com.jojo.ecommerce.adapter.out.persistence.myBatis.adapter;

import com.jojo.ecommerce.adapter.out.persistence.myBatis.mapper.ProductRankingMapper;
import com.jojo.ecommerce.application.dto.ProductRankResponse;
import com.jojo.ecommerce.application.port.out.ProductRankingRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRankingRepositoryMyBatis implements ProductRankingRepositoryPort {
    private final ProductRankingMapper mapper;

    @Override
    public List<ProductRankResponse> findTopSoldProducts(int limit, LocalDateTime from, LocalDateTime to) {
        return mapper.selectTopSoldProducts(limit, from, to);
    }
}
