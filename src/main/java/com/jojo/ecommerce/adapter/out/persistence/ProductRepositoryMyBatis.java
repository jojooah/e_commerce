package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.exception.ProductNotFoundException;
import com.jojo.ecommerce.application.port.out.ProductRepositoryPort;
import com.jojo.ecommerce.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
public class ProductRepositoryMyBatis implements ProductRepositoryPort {
    private final ProductMapper mapper;

    @Override
    public Product save(Product product) {
        mapper.insertProduct(product);
        return product;
    }

    @Override
    public Product findProductById(long productId) {
        Product p = mapper.selectProductById(productId);
        if (ObjectUtils.isEmpty(p)) {
            throw new ProductNotFoundException(productId);
        }
        return p;
    }

    @Override
    public List<Product> findAllProducts() {
        return mapper.selectAllProducts();
    }

    @Override
    public Product updateProduct(Product product) {
        int updated = mapper.updateProduct(product);
        if (updated == 0) {
            throw new ProductNotFoundException(product.getProductId());
        }
        return product;
    }
}
