package com.jojo.ecommerce.adapter.out.persistence.myBatis.mapper;

import com.jojo.ecommerce.domain.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    /**
     * 단일상품조회
     * @param productId
     * @return
     */
    Product selectProductById(@Param("productId") Long productId);

    /**
     * 전체상품조회
     * @return
     */
    List<Product> selectAllProducts();

    /**
     * 상품등록
     *
     * @param product
     */
    void insertProduct(Product product);

    /**
     * 상품 수정
     * @param product
     * @return
     */
    int updateProduct(Product product);
}
