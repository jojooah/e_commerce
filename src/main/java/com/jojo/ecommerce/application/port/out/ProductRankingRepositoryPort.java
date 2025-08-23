package com.jojo.ecommerce.application.port.out;

import com.jojo.ecommerce.application.dto.ProductRankResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRankingRepositoryPort {
    List<ProductRankResponse> findTopSoldProducts(int limit, LocalDateTime from, LocalDateTime to);
}
