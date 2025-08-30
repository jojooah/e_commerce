package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.OrderPlaceEvent;
import com.jojo.ecommerce.application.dto.PaymentCompleteEvent;
import com.jojo.ecommerce.application.port.out.DataPlatformPort;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class DataPlatformEventListener {
    private final DataPlatformPort dataPlatform;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onOrderPlaced(OrderPlaceEvent e) {
        var payload = new HashMap<String, Object>();
        payload.put("type", "ORDER_PLACED");
        payload.put("orderId", e.orderId());
        payload.put("userId", e.userId());
        payload.put("totalQuantity", e.totalQuantity());
        payload.put("totalAmount", e.totalAmount());
        payload.put("occurredAt", e.occurredAt().toString());
        dataPlatform.sendOrderEvent(payload, e.requestId());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onPaymentCompleted(PaymentCompleteEvent e) {
        var payload = new HashMap<String, Object>();
        payload.put("type", "PAYMENT_COMPLETED");
        payload.put("orderId", e.orderId());
        payload.put("paymentId", e.paymentId());
        payload.put("userId", e.userId());
        payload.put("paidAmount", e.paidAmount());
        payload.put("occurredAt", e.occurredAt().toString());
        dataPlatform.sendOrderEvent(payload, e.requestId());
    }
}
