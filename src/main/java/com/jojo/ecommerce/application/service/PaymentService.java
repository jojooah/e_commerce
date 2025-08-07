package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.CreatePaymentRequest;
import com.jojo.ecommerce.application.port.in.PaymentUseCase;
import com.jojo.ecommerce.application.port.out.*;
import com.jojo.ecommerce.domain.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService implements PaymentUseCase {
    private final OrderRepositoryPort orderRepo;
    private final PaymentRepositoryPort paymentRepo;
    private final PaymentHistoryRepositoryPort historyRepo;
    private final CouponRepositoryPort couponRepo;
    private final UserCouponRepositoryPort userCouponRepo;
    private final ProductRepositoryPort productRepo;
    private final PointRepositoryPort pointRepo;

    @Override
    @Transactional
    public Payment pay(CreatePaymentRequest createPaymentRequest) {
        Long orderId = createPaymentRequest.orderId();

        // 주문정보 가져오기
        Order order = orderRepo.findByOrderId(orderId);

        // 결제금액 계산
        int amount = order.calculateTotalPrice();

        // 쿠폰 있을경우
        if (createPaymentRequest.couponId() != null) {
            UserCoupon userCoupon = userCouponRepo.findByUserCouponId(createPaymentRequest.userId(), createPaymentRequest.couponId());
            Coupon coupon = couponRepo.findByCouponId(userCoupon.getCouponId());

            // 할인율 적용하여 금액 계산
            double discountRate = coupon.getDiscountRate();
            amount = (int) Math.round(amount * (1 - discountRate));

            // 쿠폰 사용 처리
            userCoupon.useCoupon();
            userCouponRepo.updateCoupon(userCoupon);
        }

        // 유저 포인트 차감
        Point userPoint = pointRepo.findPointByUserId(createPaymentRequest.userId());
        Point deductedPoint = userPoint.minusPoint(amount);
        pointRepo.updatePoint(deductedPoint);

        // 상품 재고 차감
        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepo.findProductById(item.getProductId());
            product.decreaseStock(item.getQuantity());

            productRepo.updateProduct(product);
        }

        Payment payment = new Payment(orderId, createPaymentRequest.userId(), createPaymentRequest.couponId(), createPaymentRequest.paymentMethod(), amount);

        // 결제 완료처리
        payment.paymentCompleted();
        Payment saved = paymentRepo.savePayment(payment);

        // 주문 상태 변경(결제완료)
        order.paymentCompleted();
        orderRepo.updateOrder(order);

        // 결제 이력 저장
        historyRepo.savePaymentHistory(new PaymentHistory(saved.getPaymentId(), STATUS_TYPE.PAYMENT_COMPLETED));

        return saved;
    }

    @Override
    @Transactional
    public boolean cancelPayment(Long paymentId) {
        // 결제 취소
        Payment payment = paymentRepo.findByPaymentId(paymentId);
        payment.paymentCanceled();
        paymentRepo.updatePayment(payment);

        // 주문상태 변경(결제 취소)
        Long orderId = payment.getOrderId();
        Order order = orderRepo.findByOrderId(orderId);

        order.paymentCanceled();
        orderRepo.updateOrder(order);

        // 유저 쿠폰 원복
        if (payment.getCouponId() != null) {
            UserCoupon userCoupon = userCouponRepo.findByUserCouponId(
                    order.getUserId(), payment.getCouponId());
            userCoupon.setUseYn("N");
            userCouponRepo.updateCoupon(userCoupon);
        }

        // 상품 재고 원복
        for (OrderItem item : order.getOrderItems()) {
            Product product = productRepo.findProductById(item.getProductId());
            product.restoreStock(item.getQuantity());
            productRepo.updateProduct(product);
        }

        // 유저 포인트 원복
        Point userPoint = pointRepo.findPointByUserId(order.getUserId());
        Point restoredPoint = userPoint.addPoint(payment.getPaymentPrice());
        pointRepo.updatePoint(restoredPoint);

        // 이력저장
        historyRepo.savePaymentHistory(new PaymentHistory(paymentId, STATUS_TYPE.PAYMENT_CANCELED));

        return true;
    }


}
