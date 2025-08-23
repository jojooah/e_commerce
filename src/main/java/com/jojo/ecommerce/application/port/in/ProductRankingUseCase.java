package com.jojo.ecommerce.application.port.in;

import com.jojo.ecommerce.application.dto.ProductRankResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRankingUseCase {
    List<ProductRankResponse> getTopSoldProducts(Integer limit, LocalDateTime from, LocalDateTime to);
}
