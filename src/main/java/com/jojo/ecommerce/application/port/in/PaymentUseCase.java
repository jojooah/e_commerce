package com.jojo.ecommerce.application.port.in;

import com.jojo.ecommerce.application.dto.CreatePaymentRequest;
import com.jojo.ecommerce.domain.model.Payment;

public interface PaymentUseCase {
    //결제하기
    Payment pay(CreatePaymentRequest createPaymentRequest);
    //결제취소
    boolean cancelPayment(Long paymentId);

}
