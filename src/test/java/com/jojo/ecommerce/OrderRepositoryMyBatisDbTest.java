package com.jojo.ecommerce;

import com.jojo.ecommerce.application.exception.OrderNotFoundException;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.domain.model.Order;
import com.jojo.ecommerce.domain.model.STATUS_TYPE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 MySQL에 연결
class OrderRepositoryMyBatisDbTest {

    @Autowired
    private OrderRepositoryPort orderRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        // 주문 테이블 초기화 (테이블명이 order라면 백틱을 사용)
        jdbcTemplate.update("DELETE FROM `orders`");
    }

    @Test
    void 주문_저장_및_조회() {
        // given
        Long userId = 42L;
        Order order = new Order(userId);

        // when
        Order saved = orderRepository.saveOrder(order);

        // then: ID가 생성되고, 조회 결과가 일치해야 한다
        assertNotNull(saved.getOrderId());
        Order found = orderRepository.findByOrderId(saved.getOrderId());
        assertEquals(saved.getOrderId(), found.getOrderId());
        assertEquals(userId, found.getUserId());
        assertEquals(STATUS_TYPE.PAYMENT_PENDING, found.getPaymentStatus());
    }

    @Test
    void 결제상태_변경() {
        // given
        Order order = new Order(50L);
        order = orderRepository.saveOrder(order);

        // when: 결제 완료
        order.paymentCompleted();
        orderRepository.updateOrder(order);
        Order updated = orderRepository.findByOrderId(order.getOrderId());
        assertEquals(STATUS_TYPE.PAYMENT_COMPLETED, updated.getPaymentStatus());

        // and when: 결제 취소
        order.paymentCanceled();
        orderRepository.updateOrder(order);
        updated = orderRepository.findByOrderId(order.getOrderId());
        assertEquals(STATUS_TYPE.PAYMENT_CANCELED, updated.getPaymentStatus());
    }

    @Test
    void 잘못된_주문ID_조회_예외() {
        // 존재하지 않는 ID 조회 시 예외
        assertThrows(OrderNotFoundException.class, () -> orderRepository.findByOrderId(999L));
    }

    @Test
    void 잘못된_주문ID_수정_예외() {
        // 존재하지 않는 ID로 수정 시 예외
        Order dummy = new Order(10L);
        dummy.setOrderId(999L);
        assertThrows(OrderNotFoundException.class, () -> orderRepository.updateOrder(dummy));
    }
}
