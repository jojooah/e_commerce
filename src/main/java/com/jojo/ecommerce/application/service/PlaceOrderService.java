package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.ProductInfo;
import com.jojo.ecommerce.application.dto.ProductOrderRequest;
import com.jojo.ecommerce.application.port.in.OrderUseCase;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.domain.model.Order;
import com.jojo.ecommerce.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceOrderService implements OrderUseCase {
    private final OrderRepositoryPort orderRepositoryPort;

    @Override
    public Order placeOrder(ProductOrderRequest productOrderRequest){
        // 주문 생성
        Order order = new Order(productOrderRequest.userId());
        order.setRequestId(productOrderRequest.requestId());

        // 상품 주문정보 돌면서 주문 아이템 생성
        for(ProductInfo productInfo : productOrderRequest.productDtoList()){
            order.addOrderItem(new OrderItem(order.getOrderId(),productInfo.productId(), productInfo.quantity(),productInfo.productPrice()));
        }

        // 주문 저장
        // [request_id, user_id] 유니크키
        return orderRepositoryPort.saveOrder(order);
    }

    @Override
    public Order cancelOrder(Long orderId){
        Order order = orderRepositoryPort.findByOrderId(orderId);
        order.paymentCanceled();

        return orderRepositoryPort.updateOrder(order);
    }

    @Override
    public Order selectOrder(Long orderId){
        return orderRepositoryPort.findByOrderId(orderId);
    }

}
