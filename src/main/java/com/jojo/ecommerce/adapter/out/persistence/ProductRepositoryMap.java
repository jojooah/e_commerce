package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.exception.ProductNotFoundException;
import com.jojo.ecommerce.application.port.out.ProductRepositoryPort;
import com.jojo.ecommerce.domain.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * port 구현체
 * Dto 아닌 Product 도메인만 다룬다.
 */
@Repository
public class ProductRepositoryMap implements ProductRepositoryPort {

    private final Map<Long, Product> repository = new ConcurrentHashMap<>();

    private Long sequence = 0L;

    @Override
    public Product save(Product product) {
        Long id = ++sequence;
        product.setProductId(id);

       repository.put(id, product);
        return repository.get(id);
    }

    @Override
    public Product findProductById(long productId) {
        if (!repository.containsKey(productId)) {
             throw new IllegalArgumentException("유효하지 않은 상품ID 입니다.");
        }

        return repository.get(productId);
    }

    @Override
    public List<Product> findAllProducts() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public Product updateProduct(Product product) {
       if(repository.get(product.getProductId()) == null){
           throw new ProductNotFoundException(product.getProductId());
       }
        repository.put(product.getProductId(), product);

        return product;

    }
}

