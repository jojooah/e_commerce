package com.jojo.ecommerce;

import com.jojo.ecommerce.application.dto.CreatePaymentRequest;
import com.jojo.ecommerce.application.dto.ProductDto;
import com.jojo.ecommerce.application.port.out.*;
import com.jojo.ecommerce.application.service.PaymentService;
import com.jojo.ecommerce.domain.model.*;
import com.jojo.ecommerce.domain.model.Order;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.jojo.ecommerce.domain.model.STATUS_TYPE.PAYMENT_CANCELED;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 결제 테스트
 * 재고차감, 포인트 차감, 쿠폰 사용여부 등..
 */
@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PaymentServiceIntegrationTest {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepositoryPort orderRepo;
    @Autowired
    private ProductRepositoryPort productRepo;
    @Autowired
    private PointRepositoryPort pointRepo;
    @Autowired
    private CouponRepositoryPort couponRepo;
    @Autowired
    private UserCouponRepositoryPort userCouponRepo;
    @Autowired
    private PaymentRepositoryPort paymentRepo;
    @Autowired
    private PaymentHistoryRepositoryPort historyRepo;

    private Order order;
    private Product apple, banana;
    private Point point;
    private UserCoupon userCoupon;
    private Coupon coupon;
    private List<ProductDto> productList;

    @BeforeAll
    void setUp() {
        // 1) 상품 세팅
        apple = productRepo.save(new Product("사과", 10, 1000, 101));
        banana = productRepo.save(new Product("바나나", 5, 1500, 102));

        productList = List.of(new ProductDto(apple.getProductId(), apple.getProductName(), 2, apple.getProductPrice(), apple.getStock()));

        // 2) 주문 세팅(apple 2개, banana 1개)
        order = new Order(42L);
        order.addOrderItem(new OrderItem(apple.getProductId(), 2, apple.getProductPrice()));
        order.addOrderItem(new OrderItem(banana.getProductId(), 1, banana.getProductPrice()));
        order = orderRepo.saveOrder(order);

        // 3) 포인트 세팅 (42번 유저 5000원)
        point = pointRepo.updatePoint(new Point(42L, 5000));

        // 4) 쿠폰 세팅 (10% 할인)
        coupon = couponRepo.saveCoupon(new Coupon("CODE123", "행사할인쿠폰", 0.1));
        userCoupon = userCouponRepo.saveCoupon(new UserCoupon(42L, coupon.getCouponId()));
    }

    @Test
    void pay_통합_테스트() {
        // given
        CreatePaymentRequest req = new CreatePaymentRequest(
                42L,
                coupon.getCouponId(),
                order.getOrderId(),
                productList,
                "CARD",
                3500
        );

        // when
        Payment payment = paymentService.pay(req);

        // then
        assertNotNull(payment.getPaymentId());

        // a) 결제 저장 확인
        Payment saved = paymentRepo.findByPaymentId(payment.getPaymentId());
        assertEquals(STATUS_TYPE.PAYMENT_COMPLETED, saved.getPaymentStatus());

        // b) 주문 상태 변경 확인
        Order paidOrder = orderRepo.findByOrderId(order.getOrderId());
        assertEquals(STATUS_TYPE.PAYMENT_COMPLETED, paidOrder.getPaymentStatus());

        // c) 재고 차감 확인
        assertEquals(8, productRepo.findProductById(apple.getProductId()).getStock());   // 10-2
        assertEquals(4, productRepo.findProductById(banana.getProductId()).getStock()); // 5-1

        // d) 포인트 차감 확인
        assertEquals(5000 - ((2 * 1000 + 1 * 1500) * 0.9), pointRepo.findPointByUserId(42L).getPoint());

        // e) 쿠폰 사용 확인
        UserCoupon uc = userCouponRepo.findByUserCouponId(42L, coupon.getCouponId());
        assertEquals("Y", uc.getUseYn());

        // f) 이력 1건 확인
        assertEquals(1, historyRepo.findByPaymentId(saved.getPaymentId()).size());
    }


    @Test
    void cancelPayment_통합_테스트() {
        // 먼저 결제
        CreatePaymentRequest req = new CreatePaymentRequest(
                42L,
                coupon.getCouponId(),
                order.getOrderId(),
                productList,
                "CARD",
                3500
        );
        Payment save = paymentService.pay(req);
        Payment find = paymentRepo.findByPaymentId(save.getPaymentId());
        Long paymentId = find.getPaymentId();
        // when
        boolean cancelResult = paymentService.cancelPayment(paymentId);

        // then
        assertTrue(cancelResult);

        // a) 결제상태 & 주문 상태 취소 확인
        assertEquals(PAYMENT_CANCELED,
                paymentRepo.findByPaymentId(paymentId).getPaymentStatus());

       assertEquals(PAYMENT_CANCELED,
                orderRepo.findByOrderId(order.getOrderId()).getPaymentStatus());

        // b) 재고 원복 확인
        assertEquals(10, productRepo.findProductById(apple.getProductId()).getStock());
        assertEquals(5, productRepo.findProductById(banana.getProductId()).getStock());

        // c) 포인트 원복 확인
        assertEquals(5000, pointRepo.findPointByUserId(42L).getPoint());

        // d) 쿠폰 원복 확인
        assertEquals("N", userCouponRepo.findByUserCouponId(42L, coupon.getCouponId()).getUseYn());

        // e) 이력 두 건(결제, 취소) 확인
        assertEquals(2, historyRepo.findByPaymentId(find.getPaymentId()).size());
    }
}
