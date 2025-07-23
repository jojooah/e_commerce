package com.jojo.ecommerce.application.port.out;

import com.jojo.ecommerce.domain.model.Product;

import java.util.List;

public interface ProductRepositoryPort {
    /**
     * 상품 저장
     * @return product
     */
    Product save(Product product);

    /**
     * 상품조회
     * @return
     */
    Product findProductById(long productId);

    /**
     * 상품 모두 조회
     * @return
     */
    List<Product> findAllProducts();


}
