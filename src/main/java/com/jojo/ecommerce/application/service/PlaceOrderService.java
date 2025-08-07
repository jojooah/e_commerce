package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.CreateOrderRequest;
import com.jojo.ecommerce.application.dto.ProductDto;
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
    public Order placeOrder(CreateOrderRequest createOrderRequest){
        // 주문 생성
        Order order = new Order(createOrderRequest.userId());
        Order saved = orderRepositoryPort.saveOrder(order);

        // 상품 주문정보 돌면서 주문 아이템 생성
        for(ProductDto productDto : createOrderRequest.productDtoList()){
            order.addOrderItem(new OrderItem(order.getOrderId(),productDto.getProductId(), productDto.getQauntity(),productDto.getProductPrice()));
        }

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
