package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.Setter;

/**
 * 유저 - 쿠폰 도메인
 * 쿠폰은 종류당 하나만 가질 수 있다.
 */
@Getter
@Setter
public class UserCoupon extends Common {
    private Long userId;
    private Long couponId;
    private Long campaignId;
    private String UseYn;

    public UserCoupon(Long userId, Long couponId) {
        this.userId = userId;
        this.couponId = couponId;
    }

    public UserCoupon(Long userId, Long couponId, Long campaignId ) {
        this.userId = userId;
        this.couponId = couponId;
        this.campaignId = campaignId;
    }

    public void useCoupon() {
        this.UseYn = "Y";
    }
}
