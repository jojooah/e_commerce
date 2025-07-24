package com.jojo.ecommerce.application.port.out;

import com.jojo.ecommerce.domain.model.UserCoupon;

public interface UserCouponRepositoryPort {
    // 유저-쿠폰정보 조회
    UserCoupon findByUserCouponId(Long userId, Long couponId);

    // 유저-쿠폰정보 수정
    UserCoupon updateCoupon(UserCoupon userCoupon);

    // 유저-쿠폰정보 저장
    UserCoupon saveCoupon(UserCoupon userCoupon);

}
