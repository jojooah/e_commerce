package com.jojo.ecommerce;

import com.jojo.ecommerce.application.port.out.CouponRepositoryPort;
import com.jojo.ecommerce.domain.model.Coupon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 MySQL에 연결
class CouponRepositoryMyBatisDbTest {
    @Autowired
    private CouponRepositoryPort couponRepository;

    @Test
    void 쿠폰저장_테스트() {
        // given
        Coupon coupon = new Coupon("TESTCODE","테스트쿠폰",10);

        // when
        // 쿠폰 저장
         couponRepository.saveCoupon(coupon);

         // 저장된 쿠폰 조회
        Coupon saved = couponRepository.findByCouponId(coupon.getCouponId());
        // then
        // 저장되었는지 확인(아이디존재하는지 확인)
        assertNotNull(saved.getCouponId());

        //쿠폰아이디로 조회
        Coupon found = couponRepository.findByCouponId(saved.getCouponId());

        //쿠폰코드, 할인율 일치하는지 확인
        assertEquals("TESTCODE", found.getCouponCode());
        assertEquals(10, found.getDiscountRate());
    }

}
