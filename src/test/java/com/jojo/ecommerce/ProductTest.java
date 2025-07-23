package com.jojo.ecommerce;

import com.jojo.ecommerce.application.dto.ProductDto;
import com.jojo.ecommerce.application.port.out.ProductRepositoryPort;
import com.jojo.ecommerce.application.service.ProductReadService;
import com.jojo.ecommerce.domain.model.Product;
import com.jojo.ecommerce.adapter.out.persistence.ProductRepositoryMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductTest {

    private ProductReadService service;
    private ProductRepositoryPort repo;

    @BeforeEach
    void setUp() {
        repo = new ProductRepositoryMap();
        service = new ProductReadService(repo);

    }

    @Test
    void 단일_상품_조회() {
        Product p = repo.save(new Product("노트북", 10, 1000, 01));
        assertEquals(1L, p.getProductId());

        ProductDto findProduct = service.findProductById(1L);
        assertEquals("노트북", findProduct.getProductName());
    }

   @Test
    void 전체_상품_조회(){
       repo.save(new Product("노트북", 10, 1000, 01));
       repo.save(new Product("데스크탑", 20, 2000, 02));
       repo.save(new Product("모니터", 30, 3000, 03));

       List<ProductDto> productList= service.findAllProducts();
       assertEquals(3, productList.size());
       assertEquals("노트북",productList.get(0).getProductName());
       assertEquals("데스크탑",productList.get(1).getProductName());
       assertEquals("모니터",productList.get(2).getProductName());
   }
}
