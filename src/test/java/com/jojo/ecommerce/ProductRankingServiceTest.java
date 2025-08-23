package com.jojo.ecommerce;

import com.jojo.ecommerce.application.dto.ProductInfo;
import com.jojo.ecommerce.application.dto.ProductOrderRequest;
import com.jojo.ecommerce.application.dto.ProductRankResponse;
import com.jojo.ecommerce.application.port.in.OrderUseCase;
import com.jojo.ecommerce.application.port.in.ProductRankingUseCase;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class ProductRankingServiceTest {

    @Autowired private OrderUseCase orderService;
    @Autowired private OrderRepositoryPort orderRepo;
    @Autowired private ProductRepositoryPort productRepo;
    @Autowired private ProductRankingUseCase rankingUseCase;

    private Product apple;
    private Product banana;
    private Product cherry;

    @BeforeEach
    void setUp() {
        // 재고 넉넉히
        apple  = productRepo.save(new Product("사과",   100, 1000, 101));
        banana = productRepo.save(new Product("바나나", 100,  500, 102));
        cherry = productRepo.save(new Product("체리",   100, 2000, 103));
    }

    // 편의 함수들
    private ProductInfo info(Product p, int qty) {
        return new ProductInfo(p.getProductId(), p.getProductName(), qty, p.getProductPrice(), p.getProductCode());
    }
    private Order place(long userId, List<ProductInfo> items) {
        int totalQty   = items.stream().mapToInt(ProductInfo::quantity).sum();
        int totalPrice = items.stream().mapToInt(i -> i.quantity() * i.productPrice()).sum();
        String requestId = "REQ-" + UUID.randomUUID();

        var req = new ProductOrderRequest(userId, null, requestId, totalQty, totalPrice, items);
        return orderService.placeOrder(req);
    }
    /** 랭킹 집계 대상이 되도록 주문을 결제완료로 마킹 */
    private void markCompleted(Order o) {
        Order persisted = orderRepo.findByOrderId(o.getOrderId());
        persisted.paymentCompleted();           // 상태: PAYMENT_COMPLETED
        orderRepo.updateOrder(persisted);       // DB 반영
    }

    @Test
    void 팔린수량_기준_Top3_랭킹_전체기간__결제완료만_집계() {
        // given: 결제완료 3건 + 취소 1건(제외)
        // o1: 사과2, 바나나5
        Order o1 = place(1L, List.of(info(apple, 2), info(banana, 5)));
        // o2: 사과1, 체리3
        Order o2 = place(2L, List.of(info(apple, 1), info(cherry, 3)));
        // o3: 바나나2, 체리1
        Order o3 = place(3L, List.of(info(banana, 2), info(cherry, 1)));
        // o4: 사과10 (취소)
        Order o4 = place(4L, List.of(info(apple, 10)));

        // 결제 완료로 마킹(집계 대상)
        markCompleted(o1);
        markCompleted(o2);
        markCompleted(o3);

        // 취소 처리(집계 제외)
        orderService.cancelOrder(o4.getOrderId());

        // when
        var top = rankingUseCase.getTopSoldProducts(3, null, null);

        // then: 비취소+완료분 집계 → 바나나 7, 체리 4, 사과 3
        assertThat(top).hasSize(3);

        assertThat(top.get(0).getProductId()).isEqualTo(banana.getProductId());
        assertThat(top.get(0).getProductName()).isEqualTo("바나나");
        assertThat(top.get(0).getTotalQuantity()).isEqualTo(7);

        assertThat(top.get(1).getProductId()).isEqualTo(cherry.getProductId());
        assertThat(top.get(1).getProductName()).isEqualTo("체리");
        assertThat(top.get(1).getTotalQuantity()).isEqualTo(4);

        assertThat(top.get(2).getProductId()).isEqualTo(apple.getProductId());
        assertThat(top.get(2).getProductName()).isEqualTo("사과");
        assertThat(top.get(2).getTotalQuantity()).isEqualTo(3);
    }

    @Test
    void 취소_주문은_랭킹에서_제외된다() {
        // given
        Order ok  = place(10L, List.of(info(apple, 2)));  // 포함
        Order cxl = place(11L, List.of(info(apple, 5)));  // 제외

        // 집계 대상: ok만 완료
        markCompleted(ok);
        // cxl은 취소 처리
        orderService.cancelOrder(cxl.getOrderId());

        // when
        var top = rankingUseCase.getTopSoldProducts(5, null, null);

        // then: 사과는 2개만 집계되어야 함
        var appleRow = top.stream()
                .filter(r -> r.getProductId() == apple.getProductId())
                .findFirst().orElseThrow();
        assertEquals(2L, appleRow.getTotalQuantity());
    }
}
