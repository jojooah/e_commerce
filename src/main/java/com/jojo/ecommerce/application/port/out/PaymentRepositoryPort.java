package com.jojo.ecommerce.application.port.out;

import com.jojo.ecommerce.domain.model.Payment;

public interface PaymentRepositoryPort {
    // 결제정보 조회
    Payment findByPaymentId(Long paymentId);

    // 결제정보 저장
    Payment savePayment(Payment payment);

    // 결제정보 수정
    Payment updatePayment(Payment payment);

}
