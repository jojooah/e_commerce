package com.jojo.ecommerce;

import com.jojo.ecommerce.application.exception.PaymentNotFoundException;
import com.jojo.ecommerce.application.port.out.PaymentRepositoryPort;
import com.jojo.ecommerce.domain.model.Payment;
import com.jojo.ecommerce.domain.model.STATUS_TYPE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 MySQL 에 연결
class PaymentRepositoryMyBatisDbTest {

    @Autowired
    private PaymentRepositoryPort paymentRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        // 매번 동일한 상태로 시작하기 위해 테이블 초기화
        jdbcTemplate.update("DELETE FROM payment");
    }

    @Test
    void 결제정보_저장_조회() {
        // given
        Long orderId   = 10L;
        Long userId    = 20L;
        Long couponId  = 30L;
        String method  = "CARD";
        int price      = 1_000;

        Payment p = new Payment(orderId, userId, couponId, method, price);
        p.setPaymentStatus(STATUS_TYPE.PAYMENT_PENDING);

        // when
        Payment saved = paymentRepository.savePayment(p);

        // then - 반환된 엔티티 검증
        assertThat(saved.getPaymentId()).isNotNull();
        assertEquals(orderId,  saved.getOrderId());
        assertEquals(userId,   saved.getUserId());
        assertEquals(couponId, saved.getCouponId());
        assertEquals(method,   saved.getPaymentMethod());
        assertEquals(price,    saved.getPaymentPrice());
        assertEquals(STATUS_TYPE.PAYMENT_PENDING, saved.getPaymentStatus());

        // and - DB 에서도 조회 가능
        Payment found = paymentRepository.findByPaymentId(saved.getPaymentId());
        assertThat(found).isNotNull();
        assertEquals(saved.getPaymentId(), found.getPaymentId());
    }

    @Test
    void 결제정보_수정() {
        // given - 먼저 저장
        Payment p = new Payment(1L, 2L, 3L, "CARD", 500);
        p.setPaymentStatus(STATUS_TYPE.PAYMENT_PENDING);
        Payment saved = paymentRepository.savePayment(p);

        // when - 상태 변경
        saved.paymentCompleted();
        Payment updated = paymentRepository.updatePayment(saved);

        // then
        assertEquals(STATUS_TYPE.PAYMENT_COMPLETED, updated.getPaymentStatus());

        // and - DB 에도 반영
        Payment found = paymentRepository.findByPaymentId(saved.getPaymentId());
        assertEquals(STATUS_TYPE.PAYMENT_COMPLETED, found.getPaymentStatus());
    }

    @Test
    void 존재하지않는_결제정보_조회_예외() {
        assertThrows(PaymentNotFoundException.class,
                () -> paymentRepository.findByPaymentId(999L));
    }

    @Test
    void 존재하지않는_결제정보_수정_예외() {
        // 없는 ID 로 업데이트 시도
        Payment p = new Payment(1L, 2L, 3L, "CARD", 100);
        p.setPaymentId(999L);
        p.setPaymentStatus(STATUS_TYPE.PAYMENT_PENDING);

        assertThrows(PaymentNotFoundException.class,
                () -> paymentRepository.updatePayment(p));
    }
}
