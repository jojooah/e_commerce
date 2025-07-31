package com.jojo.ecommerce;

import com.jojo.ecommerce.application.port.out.PaymentHistoryRepositoryPort;
import com.jojo.ecommerce.domain.model.PaymentHistory;
import com.jojo.ecommerce.domain.model.STATUS_TYPE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentHistoryRepositoryMyBatisDbTest {

    @Autowired
    private PaymentHistoryRepositoryPort repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        // 매번 동일한 상태로 시작하기 위해 초기화
        jdbcTemplate.update("DELETE FROM payment_history");
    }

    @Test
    void 결제이력_저장_및_조회() {
        // given
        Long paymentId = 42L;
        PaymentHistory h1 = new PaymentHistory(paymentId, STATUS_TYPE.PAYMENT_COMPLETED);
        PaymentHistory h2 = new PaymentHistory(paymentId, STATUS_TYPE.PAYMENT_CANCELED);

        // when
        PaymentHistory saved1 = repository.savePaymentHistory(h1);
        PaymentHistory saved2 = repository.savePaymentHistory(h2);

        // then: DB에 두 건 저장되었는지
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM payment_history WHERE payment_id = ?",
                Integer.class, paymentId
        );

        assertThat(count).isEqualTo(2);

    }
}