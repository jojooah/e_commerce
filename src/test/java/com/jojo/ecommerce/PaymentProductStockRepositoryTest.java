package com.jojo.ecommerce;

import com.jojo.ecommerce.adapter.out.persistence.OrderRepositoryMap;
import com.jojo.ecommerce.adapter.out.persistence.PaymentHistoryRepositoryMap;
import com.jojo.ecommerce.adapter.out.persistence.PaymentRepositoryMap;
import com.jojo.ecommerce.adapter.out.persistence.ProductRepositoryMap;
import com.jojo.ecommerce.application.exception.PaymentNotFoundException;
import com.jojo.ecommerce.application.port.out.OrderRepositoryPort;
import com.jojo.ecommerce.application.port.out.PaymentHistoryRepositoryPort;
import com.jojo.ecommerce.application.port.out.PaymentRepositoryPort;
import com.jojo.ecommerce.application.port.out.ProductRepositoryPort;
import com.jojo.ecommerce.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 결제 - 재고차감 테스트
 */
public class PaymentProductStockRepositoryTest {
    private OrderRepositoryPort orderRepo;
    private PaymentRepositoryPort paymentRepo;
    private ProductRepositoryPort productRepo;

    @BeforeEach
    void setUp() {
        orderRepo = new OrderRepositoryMap();
        paymentRepo = new PaymentRepositoryMap();
        productRepo = new ProductRepositoryMap();
    }

    @Test
    void 결제하면_상품재고_차감된다(){
        // 재고가 10개 있는 상품이 있다
        Product p = new Product("테스트", 10, 5000, 100);
        Product saved = productRepo.save(p);
        Long productId = saved.getProductId();

        // 3개 주문한다
        Order order = new Order(99L);
        order.addOrderItem(new OrderItem(productId, 3,5000));
        Order savedOrder = orderRepo.saveOrder(order);

        // 결제한다.
        paymentRepo.savePayment(new Payment(1L, 1L, null, "CARD", 5 * 5000));

        // 재고 차감: 주문 아이템 수량만큼 자동으로
        for (OrderItem item : savedOrder.getOrderItems()) {
            Product product= productRepo.findProductById(item.getProductId());
            product.decreaseStock(item.getQuantity());
            productRepo.updateProduct(product);
        }

        // 7개 남아있는것 확인한다
        Product after = productRepo.findProductById(productId);
        assertEquals(7, after.getStock(),
                "결제 시 decreaseStock(qty) 만큼 재고가 줄어들어야 한다");
    }

    @Test
    void 결제취소하면_상품재고_원복된다(){
        // 상품 저장
        Product banana = new Product("테스트", 5, 2000, 202);
        Product savedProduct = productRepo.save(banana);
        long productId = savedProduct.getProductId();

        // 주문 생성
        Order order = new Order(100L);
        order.addOrderItem(new OrderItem(productId, 2,2000));
        Order savedOrder = orderRepo.saveOrder(order);

        // 재고 차감
        for (OrderItem item : savedOrder.getOrderItems()) {
            productRepo.findProductById(item.getProductId())
                    .decreaseStock(item.getQuantity());
        }
        assertEquals(3, productRepo.findProductById(productId).getStock());

        // 주문 취소 처리 시 재고 원복
        for (OrderItem item : savedOrder.getOrderItems()) {
            Product p = productRepo.findProductById(item.getProductId());
            p.restoreStock(item.getQuantity());
        }

        Product afterCancel = productRepo.findProductById(productId);
        assertEquals(5, afterCancel.getStock(),
                "주문 취소 시 원래 재고만큼 재고가 복원되어야 한다");
    }

}
