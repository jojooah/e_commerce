package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PaymentHistory extends Common {
    private Long paymentHistoryId;
    private Long paymentId;
    private STATUS_TYPE paymentStatus;

    public PaymentHistory(Long paymentId, STATUS_TYPE paymentStatus) {
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
    }
}
