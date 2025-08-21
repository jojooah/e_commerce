package com.jojo.ecommerce.adapter.out.persistence.myBatis.adapter;

import com.jojo.ecommerce.adapter.out.persistence.myBatis.mapper.OrderMapper;
import com.jojo.ecommerce.application.exception.OrderNotFoundException;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.domain.model.Order;
import com.jojo.ecommerce.domain.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Primary
@Repository
@RequiredArgsConstructor
public class OrderRepositoryMyBatis implements OrderRepositoryPort {
    private final OrderMapper mapper;

    @Override
    @Transactional
    public Order saveOrder(Order order) {
        try{
            // 유니크키 제약조건
            // requestId, userId
            mapper.insertOrder(order);

            for (OrderItem item : order.getOrderItems()) {
                item.setOrderId(order.getOrderId());
                mapper.insertOrderItem(item);
            }
        } catch (DuplicateKeyException e){
            throw new DuplicateKeyException("이미 처리된 주문입니다.");
        }

        return order;
    }

    @Override
    @Transactional
    public Order updateOrder(Order order) {
            int cnt = mapper.updateOrder(order);
        if (cnt == 0) throw new OrderNotFoundException(order.getOrderId());

        for (OrderItem item : order.getOrderItems()) {
            item.setOrderId(order.getOrderId());
            mapper.updateOrderItem(item);
        }

        return order;
    }

    @Override
    public Order findByOrderId(Long orderId) {
        Order order = mapper.selectOrderById(orderId);
        if (order == null) throw new OrderNotFoundException(orderId);
        return order;
    }

}
