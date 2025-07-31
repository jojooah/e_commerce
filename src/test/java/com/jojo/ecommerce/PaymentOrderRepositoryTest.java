package com.jojo.ecommerce;

import com.jojo.ecommerce.adapter.out.persistence.OrderRepositoryMap;
import com.jojo.ecommerce.adapter.out.persistence.PaymentHistoryRepositoryMap;
import com.jojo.ecommerce.adapter.out.persistence.PaymentRepositoryMap;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.application.port.out.PaymentHistoryRepositoryPort;
import com.jojo.ecommerce.application.port.out.PaymentRepositoryPort;
import com.jojo.ecommerce.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 주문 - 결제 테스트
 */
public class PaymentOrderRepositoryTest {
    private PaymentRepositoryPort paymentRepo;
    private OrderRepositoryPort  orderRepo;
    private PaymentHistoryRepositoryPort historyRepo;

    @BeforeEach
    void setUp() {
        paymentRepo = new PaymentRepositoryMap();
        orderRepo   = new OrderRepositoryMap();
        historyRepo = new PaymentHistoryRepositoryMap();
    }

    @Test
    void 결제하면_주문도_상태바뀜(){
        //주문 저장
        Order order = new Order(10L);
        Order savedOrder = orderRepo.saveOrder(order);

        //결제한다
        Payment payment = paymentRepo.savePayment(
                new Payment(savedOrder.getOrderId(), savedOrder.getUserId(), null, "PAYPAL", 9000)
        );

        // 결제 완료
        payment.paymentCompleted();
        paymentRepo.updatePayment(payment);

        // 그리고 주문에도 상태 반영 (서비스단에서는 같이 해야)
        savedOrder.paymentCompleted();
        orderRepo.updateOrder(savedOrder);

        // 주문도 결제완료처리로 바뀌었는지 확인한다
        Order orderAfter = orderRepo.findByOrderId(savedOrder.getOrderId());
        assertEquals(STATUS_TYPE.PAYMENT_COMPLETED, orderAfter.getPaymentStatus(),
                "결제 완료 시 주문도 PAYMENT_COMPLETED로 변경되어야 한다");

        //트랜잭션..?
    }

    @Test
    void 결제_취소하면_주문도_취소됨(){
        // 결제정보 가져온다
        // 관련된 주문정보 가져온다
        Order order = new Order(20L);
        Order savedOrd = orderRepo.saveOrder(order);

        Payment payment = paymentRepo.savePayment(
                new Payment(savedOrd.getOrderId(), savedOrd.getUserId(), null, "BANK", 4500)
        );

        // 결제취소한다
        payment.paymentCanceled();
        paymentRepo.updatePayment(payment);

        // 주문에도 취소 반영 (서비스단에서는 같이 해야..)
        savedOrd.paymentCanceled();
        orderRepo.updateOrder(savedOrd);

        // 그 주문도 취소됐는지 확인한다
        Payment payAfter = paymentRepo.findByPaymentId(payment.getPaymentId());
        assertEquals(STATUS_TYPE.PAYMENT_CANCELED, payAfter.getPaymentStatus(),
                "결제 취소 후 Payment 상태가 PAYMENT_CANCELED여야 한다");

        Order ordAfter = orderRepo.findByOrderId(savedOrd.getOrderId());
        assertEquals(STATUS_TYPE.PAYMENT_CANCELED, ordAfter.getPaymentStatus(),
                "결제 취소 후 주문도 PAYMENT_CANCELED여야 한다");
        //트랜잭션..?
    }

    @Test
    void 주문_결제_이력_저장_테스트() {
        //주문 정보 만든다
        Order order = new Order(123L);
        orderRepo.saveOrder(order);

        //결제정보 만든다
        Payment payment = new Payment(
                order.getOrderId(),
                order.getUserId(),
                null,
                "CARD",
                5000
        );

        //저장한다
        Payment saved = paymentRepo.savePayment(payment);

        //이력 저장한다
        PaymentHistory history = new PaymentHistory(saved.getPaymentId(), saved.getPaymentStatus());
        PaymentHistory savedHistory = historyRepo.savePaymentHistory(history);

        //아이디 동일한지 확인한다
        assertNotNull(saved.getPaymentId(), "savePayment 후 paymentId가 세팅되어야 한다");
        assertEquals(order.getOrderId(), saved.getOrderId());
        assertEquals(savedHistory.getPaymentId(), saved.getPaymentId());
    }

}
