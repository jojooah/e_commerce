package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.domain.model.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CouponMapper {

    /**
     * 쿠폰 저장
     * @param coupon
     * @return
     */
    int insertCoupon(Coupon coupon);

    /**
     * 쿠폰조회
     * @param couponId
     * @return
     */
    Coupon selectCouponById(@Param("couponId") Long couponId);

    /**
     * 쿠폰 상태 수정
     * @param coupon
     * @return
     */
    int updateCoupon(Coupon coupon);
}
