package com.jojo.ecommerce;

import com.jojo.ecommerce.application.port.out.PointRepositoryPort;
import com.jojo.ecommerce.domain.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 MySQL에 연결
class PointRepositoryMyBatisDbTest {

    @Autowired
    private PointRepositoryPort repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 매번 동일한 상태로 시작하려면 테이블 초기화
        jdbcTemplate.execute("DELETE FROM point");
    }

    @Test
    void 포인트_충전() {
        // given
        Long userId = 123L;
        Point toSave = new Point(userId, 500);

        // when
        repository.updatePoint(toSave);

        Point save = repository.findPointByUserId(userId);

        assertEquals(500, save.getPoint());
        assertEquals(toSave.getUserId(), save.getUserId());

    }
}
