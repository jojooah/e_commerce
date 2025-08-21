package com.jojo.ecommerce.adapter.out.persistence.myBatis.adapter;


import com.jojo.ecommerce.adapter.out.persistence.myBatis.mapper.CouponMapper;
import com.jojo.ecommerce.application.exception.CouponNotFoundException;
import com.jojo.ecommerce.application.port.out.CouponRepositoryPort;
import com.jojo.ecommerce.domain.model.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Primary
@Repository
@Transactional
@RequiredArgsConstructor
public class CouponRepositoryMyBatis implements CouponRepositoryPort {
    private final CouponMapper mapper;

    @Override
    public Coupon saveCoupon(Coupon coupon) {
        mapper.insertCoupon(coupon);
        return coupon;
    }

    @Override
    public Coupon findByCouponId(Long couponId) {
        Coupon coupon = mapper.selectCouponById(couponId);
        if (coupon == null) {
            throw new CouponNotFoundException(couponId);
        }
        return coupon;
    }

}