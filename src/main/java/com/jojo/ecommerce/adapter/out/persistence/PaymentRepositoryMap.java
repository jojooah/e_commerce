package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.exception.PaymentNotFoundException;
import com.jojo.ecommerce.application.port.out.PaymentRepositoryPort;
import com.jojo.ecommerce.domain.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PaymentRepositoryMap implements PaymentRepositoryPort {
    private final Map<Long, Payment> repository = new ConcurrentHashMap<>();
    private Long sequence = 0L;

    // 결제정보 저장
    @Override
    public Payment savePayment(Payment payment){
        sequence++;
        payment.setPaymentId(sequence);

        repository.put(sequence, payment);
        return repository.get(sequence);
    }

    //결제정보 수정
    @Override
    public Payment updatePayment(Payment payment) {
        if(repository.get(payment.getPaymentId()) == null){
            throw new PaymentNotFoundException(payment.getPaymentId());
        }
        repository.put(payment.getPaymentId(),payment);

        return payment;
    }

    // 결제정보 조회
    @Override
    public Payment findByPaymentId(Long paymentId){
        if(!repository.containsKey(paymentId)){
            throw new PaymentNotFoundException(paymentId);
        }
        return repository.get(paymentId);
    }

}
