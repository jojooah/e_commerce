package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment extends Common {
    private Long paymentId;
    private Long orderId;       //주문id
    private Long userId;        //유저id
    private Long couponId;      //쿠폰id

    private String paymentMethod;           // 결제수단
    private int paymentPrice;               // 결제금액
    private STATUS_TYPE paymentStatus;      // 결제상태 초기 디폴트값 완료로?? 질문할것..

    public Payment(Long orderId, Long userId, Long couponId, String paymentMethod, int paymentPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.couponId = couponId;
        this.paymentMethod = paymentMethod;
        this.paymentPrice = paymentPrice;
    }

    public void paymentCompleted() {
        this.paymentStatus = STATUS_TYPE.PAYMENT_COMPLETED;
    }

    public void paymentCanceled() {
        this.paymentStatus = STATUS_TYPE.PAYMENT_CANCELED;
    }

}
