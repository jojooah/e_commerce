package com.jojo.ecommerce;

import com.jojo.ecommerce.application.exception.ProductNotFoundException;
import com.jojo.ecommerce.application.port.out.ProductRepositoryPort;
import com.jojo.ecommerce.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 MySQL 사용
@Transactional
class ProductRepositoryMyBatisDbTest {

    @Autowired
    private ProductRepositoryPort repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM product");
    }

    @Test
    void 상품_저장_및_조회() {
        // given
        Product toSave = new Product("테스트상품", 10, 1_000, 1234);

        // when
        Product saved = repository.save(toSave);

        // then
        assertThat(saved.getProductId()).isNotNull();
        assertThat(saved.getProductName()).isEqualTo("테스트상품");
        assertThat(saved.getStock()).isEqualTo(10);

        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM product", Integer.class
        );
        assertThat(count).isEqualTo(1);
    }

    @Test
    void 상품_전체조회() {
        // given
        repository.save(new Product("A", 1, 100, 111));
        repository.save(new Product("B", 2, 200, 222));

        // when
        List<Product> list = repository.findAllProducts();

        // then
        assertThat(list).hasSize(2);

    }

    @Test
    void 상품_수정() {
        // given
        Product p = repository.save(new Product("원본", 5, 500, 555));
        p.setProductName("수정된");
        p.setStock(3);

        // when
        Product updated = repository.updateProduct(p);

        // then
        assertThat(updated.getProductName()).isEqualTo("수정된");
        assertThat(updated.getStock()).isEqualTo(3);

        String nameInDb = jdbcTemplate.queryForObject(
                "SELECT product_name FROM product WHERE product_id = ?",
                String.class, p.getProductId()
        );
        assertThat(nameInDb).isEqualTo("수정된");
    }

    @Test
    void 상품_조회_실패_예외() {
        assertThrows(ProductNotFoundException.class,
                () -> repository.findProductById(999L));
    }

    @Test
    void 상품_수정_실패_예외() {
        Product fake = new Product("X", 1, 100, 1000);
        fake.setProductId(999L);
        assertThrows(ProductNotFoundException.class,
                () -> repository.updateProduct(fake));
    }
}
