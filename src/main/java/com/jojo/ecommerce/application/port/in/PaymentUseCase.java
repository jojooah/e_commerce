package com.jojo.ecommerce.application.port.in;

import com.jojo.ecommerce.application.dto.PaymentRequest;
import com.jojo.ecommerce.domain.model.Payment;

public interface PaymentUseCase {
    //결제하기
    Payment pay(PaymentRequest paymentRequest);
    //결제취소
    boolean cancelPayment(Long paymentId);

}
