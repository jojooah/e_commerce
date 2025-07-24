package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.exception.OrderNotFoundException;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.domain.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderRepositoryMap implements OrderRepositoryPort {
    private final Map<Long, Order> repository = new ConcurrentHashMap<>();
    private Long sequence = 0L;

    // 주문 저장
    @Override
    public Order saveOrder(Order order){
        Long id = ++sequence;
        order.setOrderId(id);
        repository.put(id, order);
        return order;
    }

    @Override
    public Order updateOrder(Order order) {
        if(repository.get(order.getOrderId()) == null){
            throw new OrderNotFoundException(order.getOrderId());
        }
        repository.put(order.getOrderId(),order);
        return order;
    }

    @Override
    public Order findByOrderId(Long orderId){
        if(orderId ==null || !repository.containsKey(orderId)){
            throw new OrderNotFoundException(orderId);
        }
        return repository.get(orderId);
    }

}
