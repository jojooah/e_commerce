package com.jojo.ecommerce.application.port.in;

import com.jojo.ecommerce.application.dto.CreatePaymentRequest;

public interface PaymentUseCase {
    //결제하기
    boolean pay(CreatePaymentRequest createPaymentRequest);
    //결제취소
    boolean cancelPayment(Long paymentId);

}
