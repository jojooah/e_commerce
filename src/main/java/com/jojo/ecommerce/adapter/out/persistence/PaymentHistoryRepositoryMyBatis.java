package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.port.out.PaymentHistoryRepositoryPort;
import com.jojo.ecommerce.domain.model.PaymentHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Primary
@Repository
@RequiredArgsConstructor
public class PaymentHistoryRepositoryMyBatis implements PaymentHistoryRepositoryPort {
    private final PaymentHistoryMapper mapper;

    @Override
    @Transactional
    public PaymentHistory savePaymentHistory(PaymentHistory history) {
        mapper.insertPaymentHistory(history);
        return history;
    }

    @Override
    public List<PaymentHistory> findByPaymentId(Long paymentId) {
        return mapper.selectByPaymentId(paymentId);
    }
}
