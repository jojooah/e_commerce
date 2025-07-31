package com.jojo.ecommerce.adapter.out.persistence;


import com.jojo.ecommerce.application.exception.UserCouponNotFoundException;
import com.jojo.ecommerce.application.port.out.UserCouponRepositoryPort;
import com.jojo.ecommerce.domain.model.UserCoupon;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Primary
@Repository
@Transactional
@RequiredArgsConstructor
public class UserCouponRepositoryMyBatis implements UserCouponRepositoryPort {
    private final UserCouponMapper mapper;

    @Override
    public UserCoupon findByUserCouponId(Long userId, Long couponId) {
        UserCoupon userCoupon = mapper.selectUserCoupon(userId, couponId);
        if (ObjectUtils.isEmpty(userCoupon)) {
            throw new UserCouponNotFoundException(couponId, userId);
        }
        return userCoupon;
    }

    @Override
    @Transactional
    public UserCoupon saveCoupon(UserCoupon userCoupon) {
        mapper.insertUserCoupon(userCoupon);
        return userCoupon;
    }

    @Override
    public boolean existsByUserCouponId(Long userId, Long couponId) {
        return mapper.existsUserCoupon(userId, couponId) > 0;
    }

    @Override
    @Transactional
    public UserCoupon updateCoupon(UserCoupon userCoupon) {
        int updated = mapper.updateUserCoupon(userCoupon);
        if (updated == 0) {
            throw new UserCouponNotFoundException(userCoupon.getCouponId(), userCoupon.getUserId());
        }

        return mapper.selectUserCoupon(userCoupon.getUserId(), userCoupon.getCouponId());
    }
}