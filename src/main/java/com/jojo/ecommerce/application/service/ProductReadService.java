package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.ProductDto;
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

    public ProductDto findProductById(long productId){
        Product product = productRepositoryPort.findProductById(productId);
        return ProductDto.of(product);
    }

    @Override
    public List<ProductDto> findAllProducts() {
        List<Product> products = productRepositoryPort.findAllProducts();
        return products.stream()
                .map(ProductDto::of)
                .collect(Collectors.toList());
    }

}
