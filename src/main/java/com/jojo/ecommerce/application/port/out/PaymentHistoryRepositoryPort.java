package com.jojo.ecommerce.application.port.out;

import com.jojo.ecommerce.domain.model.PaymentHistory;

import java.util.List;

public interface PaymentHistoryRepositoryPort {
    // 결제이력 조회
    List<PaymentHistory> findByPaymentId(Long paymentId);

    // 결제이력 저장
    PaymentHistory savePaymentHistory(PaymentHistory paymentHistory);

}
