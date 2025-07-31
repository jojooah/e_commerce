package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.domain.model.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserCouponMapper {
    /**
     *  유저-쿠폰 조회
     * @param userId
     * @param couponId
     * @return
     */
    UserCoupon selectUserCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /**
     * 쿠폰발행
     * @param userCoupon
     * @return
     */
    int insertUserCoupon(UserCoupon userCoupon);

    /**
     * 쿠폰정보 수정(사용/미사용)
     * @param userCoupon
     * @return
     */
    int updateUserCoupon(UserCoupon userCoupon);

    /**
     * 쿠폰 존재하는지 확인
     * @param userId
     * @param couponId
     * @return
     */
    int existsUserCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
}
