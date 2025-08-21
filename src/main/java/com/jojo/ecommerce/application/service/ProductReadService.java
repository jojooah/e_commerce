package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.ProductResponse;
import com.jojo.ecommerce.application.port.in.ProductReadUseCase;
import com.jojo.ecommerce.application.port.out.ProductRepositoryPort;
import com.jojo.ecommerce.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 상품조회 유스케이스 구현체
 * Dto 리턴한다.
 */
@Service
@RequiredArgsConstructor
public class ProductReadService implements ProductReadUseCase {

    private final ProductRepositoryPort productRepositoryPort;

    public ProductResponse findProductById(long productId){
        Product product = productRepositoryPort.findProductById(productId);
        return ProductResponse.of(product);
    }

    @Override
    public List<ProductResponse> findAllProducts() {
        List<Product> products = productRepositoryPort.findAllProducts();
        return products.stream()
                .map(ProductResponse::of)
                .toList();
    }

}
