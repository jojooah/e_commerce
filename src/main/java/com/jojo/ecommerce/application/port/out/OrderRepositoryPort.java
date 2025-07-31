package com.jojo.ecommerce.application.port.out;

import com.jojo.ecommerce.domain.model.Order;

public interface OrderRepositoryPort {
    // 주문저장
    Order saveOrder(Order order);

    // 주문 수정
    Order updateOrder(Order order);

    // 주문 조회
    Order findByOrderId(Long orderId);


}
