package com.jojo.ecommerce;

import com.jojo.ecommerce.application.dto.CreateOrderRequest;
import com.jojo.ecommerce.application.dto.ProductDto;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.application.port.out.ProductRepositoryPort;
import com.jojo.ecommerce.application.service.PlaceOrderService;
import com.jojo.ecommerce.domain.model.Order;
import com.jojo.ecommerce.domain.model.OrderItem;
import com.jojo.ecommerce.domain.model.Product;
import com.jojo.ecommerce.domain.model.STATUS_TYPE;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class PlaceOrderSrviceIntegrationTest {

    @Autowired
    private PlaceOrderService orderService;

    @Autowired
    private OrderRepositoryPort orderRepo;

    @Autowired
    private ProductRepositoryPort productRepo;

    private Product apple;
    private Product banana;
    private List<ProductDto> productDtos;

    @BeforeEach
    void setUp() {
        // 상품 저장
        apple = productRepo.save(new Product("사과", 10, 1000, 101));
        banana = productRepo.save(new Product("바나나", 5, 1500, 102));

        // DTO 리스트 준비
        // 사과 2개, 바나나1개 담음
        productDtos = List.of(new ProductDto(apple.getProductId(), apple.getProductName(), 2, apple.getProductPrice(), apple.getProductCode()), new ProductDto(banana.getProductId(), banana.getProductName(), 1, banana.getProductPrice(), banana.getProductCode()));
    }

    @Test
    void placeOrder_정상_동작() {
        // given
        CreateOrderRequest req = new CreateOrderRequest(
                100L,               // userId
                null,               // couponId
                3,                  // totalQuantity
                2 * apple.getProductPrice() + 1 * banana.getProductPrice(),
                productDtos);

        // when
        Order order = orderService.placeOrder(req);

        // then
        assertNotNull(order.getOrderId(), "OrderId가 부여되어야 한다");
        Order persisted = orderRepo.findByOrderId(order.getOrderId());

        assertEquals(100L, persisted.getUserId());
        assertEquals(STATUS_TYPE.PAYMENT_PENDING, persisted.getPaymentStatus());
        assertEquals(2, persisted.getOrderItems().size());

        OrderItem item1 = persisted.getOrderItems().stream().filter(i -> i.getProductId().equals(apple.getProductId())).findFirst().orElseThrow();
        assertEquals(2, item1.getQuantity());
   //     assertEquals(1000, item1.getUnitPrice());

        OrderItem item2 = persisted.getOrderItems().stream().filter(i -> i.getProductId().equals(banana.getProductId())).findFirst().orElseThrow();
        assertEquals(1, item2.getQuantity());
      //  assertEquals(1500, item2.getUnitPrice());
    }

    @Test
    void cancelOrder_주문_취소_동작() {
        // given: 주문 생성
        CreateOrderRequest req = new CreateOrderRequest(200L, null, 2, 2 * apple.getProductPrice(), List.of(new ProductDto(apple.getProductId(), apple.getProductName(), 2, apple.getProductPrice(), apple.getStock())));
        Order order = orderService.placeOrder(req);

        // when: 주문 취소
        Order canceled = orderService.cancelOrder(order.getOrderId());

        // then
        assertEquals(STATUS_TYPE.PAYMENT_CANCELED, canceled.getPaymentStatus());
        Order fromRepo = orderRepo.findByOrderId(order.getOrderId());
   //     assertEquals(STATUS_TYPE.PAYMENT_CANCELED, fromRepo.getPaymentStatus());
    }
}
