package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.exception.PaymentNotFoundException;
import com.jojo.ecommerce.application.port.out.PaymentRepositoryPort;
import com.jojo.ecommerce.domain.model.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Primary
@Repository
@Transactional
@RequiredArgsConstructor
public class PaymentRepositoryMyBatis implements PaymentRepositoryPort {
    private final PaymentMapper mapper;

    @Override
    public Payment savePayment(Payment payment) {
        mapper.insertPayment(payment);
        return payment;
    }

    @Override
    public Payment updatePayment(Payment payment) {
        int updated = mapper.updatePayment(payment);
        if (updated == 0) {
            throw new PaymentNotFoundException(payment.getPaymentId());
        }
        return payment;
    }

    @Override
    public Payment findByPaymentId(Long paymentId) {
        Payment p = mapper.selectPaymentById(paymentId);
        if (ObjectUtils.isEmpty(p)) {
            throw new PaymentNotFoundException(paymentId);
        }
        return p;
    }
}
