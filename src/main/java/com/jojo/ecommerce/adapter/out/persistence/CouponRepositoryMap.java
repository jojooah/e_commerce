package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.exception.CouponNotFoundException;
import com.jojo.ecommerce.application.port.out.CouponRepositoryPort;
import com.jojo.ecommerce.domain.model.Coupon;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CouponRepositoryMap implements CouponRepositoryPort {
    private final Map<Long, Coupon> repository = new ConcurrentHashMap<>();
    private Long sequence = 0L;

    @Override
    public Coupon saveCoupon(Coupon coupon) {
        Long id = ++sequence;
        coupon.setCouponId(id);
        repository.put(id, coupon);
        return coupon;
    }

    @Override
    public Coupon findByCouponId(Long couponId) {
        if (!repository.containsKey(couponId)) {
            throw new CouponNotFoundException(couponId);
        }
        return repository.get(couponId);
    }

    @Override
    public Coupon updateCoupon(Coupon coupon) {
        Long couponId = coupon.getCouponId();
        if (repository.get(couponId) == null) {
            throw new CouponNotFoundException(couponId);
        }
        repository.put(couponId, coupon);
        return coupon;
    }
}
