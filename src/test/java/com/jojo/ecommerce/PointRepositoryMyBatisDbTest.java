package com.jojo.ecommerce;

import com.jojo.ecommerce.adapter.out.persistence.PointRepositoryMyBatis;
import com.jojo.ecommerce.domain.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 MySQL에 연결
class PointRepositoryMyBatisDbTest {

    @Autowired
    private PointRepositoryMyBatis repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 매번 동일한 상태로 시작하려면 테이블 초기화
        jdbcTemplate.execute("DELETE FROM point");
    }

    @Test
    void updatePoint_insertsRowInRealDb() {
        // given
        Long userId = 123L;
        Point toSave = new Point(userId, 500);

        // when
        repository.updatePoint(toSave);

        // then: DB에 한 건이 들어갔는지 확인
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM point WHERE user_id = ?",
                Integer.class,
                userId
        );
        assertThat(count).isEqualTo(1);

        // 그리고 값도 정확한지 확인
        Integer stored = jdbcTemplate.queryForObject(
                "SELECT point FROM point WHERE user_id = ?",
                Integer.class,
                userId
        );
        assertThat(stored).isEqualTo(500);
    }
}
