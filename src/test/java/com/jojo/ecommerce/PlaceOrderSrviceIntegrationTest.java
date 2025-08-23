package com.jojo.ecommerce;

import com.jojo.ecommerce.application.dto.ProductInfo;
import com.jojo.ecommerce.application.dto.ProductOrderRequest;
import com.jojo.ecommerce.application.port.in.OrderUseCase;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.application.port.out.ProductRepositoryPort;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PlaceOrderSrviceIntegrationTest {

    @Autowired
    private OrderUseCase orderService;

    @Autowired
    private OrderRepositoryPort orderRepo;

    @Autowired
    private ProductRepositoryPort productRepo;

    private Product apple;
    private Product banana;
    private List<ProductInfo> productInfos;

    @BeforeEach
    void setUp() {
        // 상품 저장
        apple = productRepo.save(new Product("사과", 10, 1000, 101));
        banana = productRepo.save(new Product("바나나", 5, 1500, 102));

        // DTO 리스트 준비
        // 사과 2개, 바나나1개 담음
        productInfos = List.of(new ProductInfo(apple.getProductId(), apple.getProductName(), 2, apple.getProductPrice(), apple.getProductCode()), new ProductInfo(banana.getProductId(), banana.getProductName(), 1, banana.getProductPrice(), banana.getProductCode()));
    }

    @Test
    void placeOrder_정상_동작() {
        // given
        ProductOrderRequest req = new ProductOrderRequest(
                100L,               // userId
                null,               // couponId
                "REQ-1234",
                3,                  // totalQuantity
                2 * apple.getProductPrice() + 1 * banana.getProductPrice(),
                productInfos);

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
        assertEquals(1000, item1.getUnitPrice());

        OrderItem item2 = persisted.getOrderItems().stream().filter(i -> i.getProductId().equals(banana.getProductId())).findFirst().orElseThrow();
        assertEquals(1, item2.getQuantity());
        assertEquals(1500, item2.getUnitPrice());
    }

    @Test
    void cancelOrder_주문_취소_동작() {
        // given: 주문 생성
        ProductOrderRequest req = new ProductOrderRequest(200L, null,   "REQ-1234", 2, 2 * apple.getProductPrice(), List.of(new ProductInfo(apple.getProductId(), apple.getProductName(), 2, apple.getProductPrice(), apple.getStock())));
        Order order = orderService.placeOrder(req);

        // when: 주문 취소
        Order canceled = orderService.cancelOrder(order.getOrderId());

        // then
        assertEquals(STATUS_TYPE.PAYMENT_CANCELED, canceled.getPaymentStatus());
        Order fromRepo = orderRepo.findByOrderId(order.getOrderId());
        assertEquals(STATUS_TYPE.PAYMENT_CANCELED, fromRepo.getPaymentStatus());
    }

    @Test
    void 동시_주문_같은_requestId_한건만_생성() throws Exception {
        final Long userId = 1000L;
        final String requestId = "REQ-" + UUID.randomUUID();
        int totalQty = 3;
        int totalAmount = 2 * apple.getProductPrice() + 1 * banana.getProductPrice();

        ProductOrderRequest req = new ProductOrderRequest(
                userId, null, requestId, totalQty, totalAmount, productInfos
        );

        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(threads);
        ConcurrentLinkedQueue<Order> results = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    Order o = orderService.placeOrder(req);
                    results.add(o);
                } catch (Exception e) {
                    // 예외가 나면 테스트 실패로 본다(멱등이라면 기존 주문 반환이 정석)
                    fail("Unexpected exception: " + e);
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(20, TimeUnit.SECONDS), "동시 작업 타임아웃");
        pool.shutdownNow();

        // 결과로 반환된 orderId는 모두 같아야 함(한 건만 생성)
        Set<Long> distinctIds = results.stream().map(Order::getOrderId).collect(Collectors.toSet());
        assertEquals(1, distinctIds.size(), "같은 requestId로는 주문이 1건만 생성되어야 한다");

        Long orderId = distinctIds.iterator().next();
        Order persisted = orderRepo.findByOrderId(orderId);
        assertEquals(userId, persisted.getUserId());
        assertEquals(STATUS_TYPE.PAYMENT_PENDING, persisted.getPaymentStatus());
        assertEquals(2, persisted.getOrderItems().size());

        // 아이템 검증
        OrderItem a = persisted.getOrderItems().stream()
                .filter(i -> i.getProductId().equals(apple.getProductId()))
                .findFirst().orElseThrow();
        assertEquals(2, a.getQuantity());
        assertEquals(apple.getProductPrice(), a.getUnitPrice());

        OrderItem b = persisted.getOrderItems().stream()
                .filter(i -> i.getProductId().equals(banana.getProductId()))
                .findFirst().orElseThrow();
        assertEquals(1, b.getQuantity());
        assertEquals(banana.getProductPrice(), b.getUnitPrice());
    }

}
