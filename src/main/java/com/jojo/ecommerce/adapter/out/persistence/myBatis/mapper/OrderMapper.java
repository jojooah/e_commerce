package com.jojo.ecommerce.adapter.out.persistence.myBatis.mapper;

import com.jojo.ecommerce.domain.model.Order;
import com.jojo.ecommerce.domain.model.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderMapper {

    /**
     * 단일 주문 조회
     * @param orderId
     * @return
     */
    Order selectOrderById(@Param("orderId") Long orderId);

    /**
     * 주문 저장
     * @param order
     * @return
     */
    int insertOrder(Order order);

    /**
     * 주문 업데이트
     * @param order
     * @return
     */
    int updateOrder(Order order);

    /**
     * 주문상품 저장
     * @param item
     * @return
     */
    int insertOrderItem(OrderItem item);

    /**
     * 주문상품 상태 변경
     * @param item
     * @return
     */
    int updateOrderItem(OrderItem item);
}
