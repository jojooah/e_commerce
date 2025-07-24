package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.domain.Common;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coupon extends Common {
    private Long couponId;
    private String couponCode;  // 쿠폰코드
    private String couponName;  // 쿠폰이름
    private double discountRate;   // 할인율

    public Coupon(String couponCode, String couponName, double discountRate) {
        this.couponCode = couponCode;
        this.couponName = couponName;
        this.discountRate = discountRate;
    }
}
