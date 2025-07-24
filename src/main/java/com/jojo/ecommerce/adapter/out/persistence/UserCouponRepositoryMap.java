package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.exception.UserCouponNotFoundException;
import com.jojo.ecommerce.application.port.out.UserCouponRepositoryPort;
import com.jojo.ecommerce.domain.model.UserCoupon;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserCouponRepositoryMap implements UserCouponRepositoryPort {
    private final Map<String, UserCoupon> repository = new ConcurrentHashMap<>();

    private String makeKey(Long userId, Long couponId) {
        return userId + ":" + couponId;
    }

    @Override
    public UserCoupon findByUserCouponId(Long userId, Long couponId) {
        String key = makeKey(userId, couponId);
        UserCoupon uc = repository.get(key);
        if (uc == null) {
            throw new UserCouponNotFoundException(couponId,userId);
        }
        return uc;
    }

    @Override
    public UserCoupon saveCoupon(UserCoupon userCoupon) {
        String key = makeKey(userCoupon.getUserId(), userCoupon.getCouponId());
        repository.put(key, userCoupon);
        return userCoupon;
    }

    @Override
    public UserCoupon updateCoupon(UserCoupon userCoupon) {
        String key = makeKey(userCoupon.getUserId(), userCoupon.getCouponId());
        if (repository.get(key) == null) {
            throw new UserCouponNotFoundException(userCoupon.getCouponId(), userCoupon.getUserId());
        }
        repository.put(key, userCoupon);
        return userCoupon;
    }
}
