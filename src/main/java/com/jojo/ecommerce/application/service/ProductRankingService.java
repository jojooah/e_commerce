package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.ProductRankResponse;
import com.jojo.ecommerce.application.port.in.ProductRankingUseCase;
import com.jojo.ecommerce.application.port.out.ProductRankingRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRankingService implements ProductRankingUseCase {
    private final ProductRankingRepositoryPort repo;
    @Override
    public List<ProductRankResponse> getTopSoldProducts(Integer limit, LocalDateTime from, LocalDateTime to) {
        int top = (limit == null || limit <= 0) ? 5 : limit;
        return repo.findTopSoldProducts(top, from, to);
    }
}
