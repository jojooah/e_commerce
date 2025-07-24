package com.jojo.ecommerce.application.port.in;

import com.jojo.ecommerce.application.dto.CreateOrderRequest;
import com.jojo.ecommerce.domain.model.Order;

public interface OrderUseCase {

    // 주문하기
    Order placeOrder(CreateOrderRequest createOrderRequest);

    // 주문취소
    Order cancelOrder(Long orderId);

    // 주문조회
    Order selectOrder(Long orderId);
}
