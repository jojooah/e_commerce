package com.jojo.ecommerce.application.port.in;

import com.jojo.ecommerce.application.dto.ProductDto;

import java.util.List;

public interface ProductReadUseCase {

    /**
     * 단일 상품 조회
     * @param productId
     * @return
     */
    ProductDto findProductById(long productId);

    /**
     * 모든 상품 조회
     * @return
     */
    List<ProductDto> findAllProducts();

}
