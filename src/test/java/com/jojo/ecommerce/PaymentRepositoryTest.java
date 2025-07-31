package com.jojo.ecommerce;

import com.jojo.ecommerce.adapter.out.persistence.PaymentRepositoryMap;
import com.jojo.ecommerce.application.exception.PaymentNotFoundException;
import com.jojo.ecommerce.application.port.out.PaymentRepositoryPort;
import com.jojo.ecommerce.domain.model.Payment;
import com.jojo.ecommerce.domain.model.STATUS_TYPE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentRepositoryTest {
    private PaymentRepositoryPort paymentRepo;

    @BeforeEach
    void setUp() {
        paymentRepo = new PaymentRepositoryMap();
    }

    @Test
    void 결제_저장_테스트() {

        //결제정보 만든다
        Payment payment = new Payment(
                1L,
                1L,
                null,
                "CARD",
                5000
        );

        //저장한다
        Payment saved = paymentRepo.savePayment(payment);

        //아이디 동일한지 확인한다
        assertNotNull(saved.getPaymentId(), "savePayment 후 paymentId가 세팅되어야 한다");
        assertEquals(1L, saved.getOrderId());
        assertEquals(1L, saved.getPaymentId());
    }

    @Test
    void 결제_상태_변경_테스트() throws Exception {
        // 결제 정보 만든다
        Payment payment = paymentRepo.savePayment(new Payment(1L, 1L, null, "CARD", 5000));

        // 상태변경한다
        // 저장한다
        payment.paymentCompleted();
        Payment updated = paymentRepo.updatePayment(payment);

        // 꺼내서 바뀐 상태인지 확인한다
        assertEquals(STATUS_TYPE.PAYMENT_COMPLETED, updated.getPaymentStatus());

    }

    @Test
    void 잘못된_결제ID_처리_테스트() {
        //이상한 아이디로 조회해본다
        Payment payment = new Payment(1L, 1L, null, "CARD", 1000);
        payment.setPaymentId(999L);

        //익셉션 터지는지 확인한다
        assertThrows(PaymentNotFoundException.class,
                () -> paymentRepo.updatePayment(payment),
                "존재하지 않는 paymentId로 updatePayment 호출 시 PaymentNotFoundException 발생");

    }


}
