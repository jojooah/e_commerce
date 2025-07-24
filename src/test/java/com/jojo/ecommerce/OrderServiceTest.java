package com.jojo.ecommerce;

import com.jojo.ecommerce.adapter.out.persistence.OrderRepositoryMap;
import com.jojo.ecommerce.application.dto.CreateOrderRequest;
import com.jojo.ecommerce.application.dto.ProductDto;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.application.service.PlaceOrderService;
import com.jojo.ecommerce.domain.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderServiceTest {
    private PlaceOrderService service;
    private OrderRepositoryPort repo;

    @BeforeEach
    void setUp() {
        repo = new OrderRepositoryMap();
        service = new PlaceOrderService(repo);

    }

    @Test
    void 주문_생성_테스트(){
        List<ProductDto> productDtoList = new ArrayList<>();
        productDtoList.add(new ProductDto(1L, "상품1", 2, 1000, 1));
        productDtoList.add(new ProductDto(2L, "상품2", 1, 2000, 2));

        CreateOrderRequest req = new CreateOrderRequest(
                100L,      // userId
                null,      // couponId
                3,         // totalQuantity (2+1)
                3000,      // totalPrice
                productDtoList
        );

        //  주문 생성
        Order order = service.placeOrder(req);

        // 반환된 Order 에서 검증
        assertNotNull(order.getOrderId(), "placeOrder 리턴값에 orderId가 세팅되어야 한다");
        assertEquals(2, order.getOrderItems().size(), "아이템은 2개가 담겨야 한다");

        // 실제 저장소에도 반영되었는지
        Order persisted = repo.findByOrderId(order.getOrderId());
        assertNotNull(persisted, "저장소에서 조회된 Order가 null 이 아니어야 한다");
        assertEquals(2, persisted.getOrderItems().size(), "저장된 Order에 2개의 아이템이 있어야 한다");

    }
}
