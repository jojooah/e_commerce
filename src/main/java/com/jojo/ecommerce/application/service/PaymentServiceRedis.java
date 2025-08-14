package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.CreatePaymentRequest;
import com.jojo.ecommerce.application.exception.AlreadyCompletedOrder;
import com.jojo.ecommerce.application.exception.ConcurrencyBusyException;
import com.jojo.ecommerce.application.port.in.PaymentUseCase;
import com.jojo.ecommerce.application.port.out.*;
import com.jojo.ecommerce.domain.model.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Primary
@Service
@RequiredArgsConstructor
public class PaymentServiceRedis implements PaymentUseCase {
    private final RedissonClient redisson;

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
        if (createPaymentRequest.orderId() == null) {
            throw new IllegalArgumentException("주문 시퀀스가 없습니다");
        }
        if (createPaymentRequest.userId() == null) {
            throw new IllegalArgumentException("유저 시퀀스가 없습니다");
        }


        // 주문락 생성
        RLock orderLock = redisson.getLock("lock:order:" + createPaymentRequest.orderId());
        // 포인트락 생성
        RLock pointLock = redisson.getLock("lock:point:" + createPaymentRequest.userId());
        // 상품락 생성
        List<RLock> productLocks = orderRepo.findByOrderId(createPaymentRequest.orderId()).getOrderItems().stream()
                .sorted(Comparator.comparing(OrderItem::getProductId)) // 항장 정렬. 데드락 방지!
                .map(orderItem -> redisson.getLock("lock:product:" + orderItem.getProductId()))
                .toList();
        // 쿠폰락 생성
        RLock couponLock = null;
        if (createPaymentRequest.couponId() != null) {
            couponLock = redisson.getLock("lock:coupon:" + createPaymentRequest.userId() + ":" + createPaymentRequest.couponId());
        }
 
        // 멀티락: 항상 같은 순서로. 데드락 방지
        List<RLock> locks = new ArrayList<>();
        // 1. 주문 락 추가
        locks.add(orderLock);
        // 2. 포인트 락 추가
        locks.add(pointLock);
        // 3. 상품 락들 추가
        locks.addAll(productLocks);
        // 4. 쿠폰락 추가
        if (couponLock != null) locks.add(couponLock);
        // 5. 리스트를 배열로 변환해서 MultiLock 생성
        RLock[] lockArray = locks.toArray(new RLock[0]);

        RLock multi = redisson.getMultiLock(lockArray);

        boolean locked = false;

        //====== 결제로직 =====
        try {
            locked = multi.tryLock(2, 30, TimeUnit.SECONDS); // 최대 2초 대기, 30초 리스니
            if (!locked) {
                throw new ConcurrencyBusyException("잠금 획득 실패");
            }

            // ===주문정보 가져오기===
            Long orderId = createPaymentRequest.orderId();
            Order order = orderRepo.findByOrderId(orderId);

            // 이미 완료된 주문일경우 예외처리
            if (STATUS_TYPE.PAYMENT_COMPLETED == order.getPaymentStatus())
                throw new AlreadyCompletedOrder();

            // ===결제금액 계산===
            int amount = order.calculateTotalPrice();

            // ===쿠폰 있을경우===
            if (createPaymentRequest.couponId() != null) {
                // 쿠폰 정보 조회
                UserCoupon userCoupon = userCouponRepo.findByUserCouponId(createPaymentRequest.userId(), createPaymentRequest.couponId());
                Coupon coupon = couponRepo.findByCouponId(userCoupon.getCouponId());

                // 할인율 적용하여 금액 계산
                double discountRate = coupon.getDiscountRate();
                amount = (int) Math.round(amount * (1 - discountRate));

                // 쿠폰 사용 처리
                userCoupon.useCoupon();
                userCouponRepo.updateCoupon(userCoupon);
            }

            // ===유저 포인트 차감===
            // 유저 포인트 조회
            Point userPoint = pointRepo.findPointByUserId(createPaymentRequest.userId());

            // 유저 포인트 차감
            Point deductedPoint = userPoint.minusPoint(amount);
            pointRepo.updatePoint(deductedPoint);

            // ===상품 재고 차감===
            for (OrderItem item : order.getOrderItems()) {
                // 상품 조회
                Product product = productRepo.findProductById(item.getProductId());

                //상품 재고 차감
                product.decreaseStock(item.getQuantity());
                productRepo.updateProduct(product);
            }
            // ===결제 완료처리===
            // 결제 정보 생성
            Payment payment = new Payment(orderId, createPaymentRequest.userId(), createPaymentRequest.couponId(), createPaymentRequest.paymentMethod(), amount);
            // 결제 상태 "완료"로 변경
            payment.paymentCompleted();
            // 결제정보 저장
            Payment saved = paymentRepo.savePayment(payment);

            // ===주문 상태 변경(결제완료)===
            order.paymentCompleted();
            orderRepo.updateOrder(order);

            // ===결제 이력 저장(결제완료)===
            historyRepo.savePaymentHistory(new PaymentHistory(saved.getPaymentId(), STATUS_TYPE.PAYMENT_COMPLETED));

            return saved;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConcurrencyBusyException("락 대기 중 인터럽트");
        } finally {
            //  락 해제??
            if (locked) {
                try {
                    multi.unlock();
                } catch (Exception ignore) {
                }
            }
        }
    }

    @Override
    @Transactional
    public boolean cancelPayment(Long paymentId) {
        Payment payment = paymentRepo.findByPaymentId(paymentId);
        Order order = orderRepo.findByOrderId(payment.getOrderId());

        // 주문락 생성
        RLock orderLock = redisson.getLock("lock:order:" + payment.getOrderId());
        // 포인트락 생성
        RLock pointLock = redisson.getLock("lock:point:" + payment.getUserId());
       // 상품락 생성
        List<RLock> productLocks = order.getOrderItems().stream()
                .sorted(Comparator.comparing(OrderItem::getProductId)) // 항장 정렬. 데드락 방지!
                .map(orderItem -> redisson.getLock("lock:product:" + orderItem.getProductId()))
                .toList();
        // 쿠폰락 생성
        RLock couponLock = null;
        if (payment.getCouponId() != null) {
            couponLock = redisson.getLock("lock:coupon:" + payment.getUserId() + ":" + payment.getCouponId());
        }
    
        // 멀티락: 항상 같은 순서로. 데드락 방지
        List<RLock> locks = new ArrayList<>();
        // 1. 주문락추가
        locks.add(orderLock);
        // 2. 포인트 락 추가
        locks.add(pointLock);
        // 3. 상품 락들 추가
        locks.addAll(productLocks);
        // 4. 쿠폰락 추가
        if (couponLock != null) locks.add(couponLock);
        // 5. 리스트를 배열로 변환해서 MultiLock 생성
        RLock[] lockArray = locks.toArray(new RLock[0]);

        RLock multi = redisson.getMultiLock(lockArray);

        boolean locked = false;

        //====== 결제로직 =====
        try {
            // tryLock 성공 직후 다시 조회한다.
            // 락을 잡기 전 사이에 다른 트랜잭션이 상태를 바꿨다면 구 버전 객체로 업데이트하게 된다.
            payment = paymentRepo.findByPaymentId(paymentId);
            order   = orderRepo.findByOrderId(payment.getOrderId());

            locked = multi.tryLock(2, 30, TimeUnit.SECONDS); // 최대 2초 대기, 30초 리스
            if (!locked) {
                throw new ConcurrencyBusyException("잠금 획득 실패");
            }

            // === 결제 취소 ===
            payment.paymentCanceled();
            paymentRepo.updatePayment(payment);

            // === 주문 취소 ===
            order.paymentCanceled();
            orderRepo.updateOrder(order);

            //=== 유저 쿠폰 원복 ===
            if (payment.getCouponId() != null) {
                // 쿠폰 조회
                UserCoupon userCoupon = userCouponRepo.findByUserCouponId(order.getUserId(), payment.getCouponId());
                // 사용여부 "N"으로 변경
                userCoupon.setUseYn("N");
                userCouponRepo.updateCoupon(userCoupon);
            }

            // === 상품 재고 원복 ===
            for (OrderItem item : order.getOrderItems()) {
                // 상품 조회
                Product product = productRepo.findProductById(item.getProductId());
                // 상품 개수 원복
                product.restoreStock(item.getQuantity());
                productRepo.updateProduct(product);
            }

            // === 유저 포인트 원복 ===
            // 유저 포인트 조회
            Point userPoint = pointRepo.findPointByUserId(order.getUserId());
            // 유저 포인트 원복
            Point restoredPoint = userPoint.addPoint(payment.getPaymentPrice());
            pointRepo.updatePoint(restoredPoint);

            //=== 이력저장 ===
            historyRepo.savePaymentHistory(new PaymentHistory(paymentId, STATUS_TYPE.PAYMENT_CANCELED));

            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConcurrencyBusyException("락 대기 중 인터럽트");
        } finally {
            //  락 해제??
            if (locked) {
                try {
                    multi.unlock();
                } catch (Exception ignore) {
                }
            }
        }
    }

}
