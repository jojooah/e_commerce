package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.port.out.PaymentHistoryRepositoryPort;
import com.jojo.ecommerce.domain.model.PaymentHistory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PaymentHistoryRepositoryMap implements PaymentHistoryRepositoryPort {
    private final Map<Long, PaymentHistory> repository = new ConcurrentHashMap<>();
    private Long sequence = 0L;

    @Override
    public PaymentHistory savePaymentHistory(PaymentHistory history) {
        history.setPaymentHistoryId(++sequence);
        repository.put(sequence,history);
        return history;
    }

    @Override
    public List<PaymentHistory> findByPaymentId(Long paymentId) {
        return new ArrayList<>(repository.values());
    }
}
