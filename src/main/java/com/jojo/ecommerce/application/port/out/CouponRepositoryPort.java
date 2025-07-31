package com.jojo.ecommerce.application.port.out;

import com.jojo.ecommerce.domain.model.Coupon;

public interface CouponRepositoryPort {
    // 쿠폰정보 조회
    Coupon findByCouponId(Long couponId);

    // 쿠폰정보 저장
    Coupon saveCoupon(Coupon coupon);

}
