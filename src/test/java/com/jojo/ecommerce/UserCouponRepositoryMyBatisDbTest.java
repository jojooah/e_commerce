package com.jojo.ecommerce;

import com.jojo.ecommerce.application.exception.UserCouponNotFoundException;
import com.jojo.ecommerce.application.port.out.UserCouponRepositoryPort;
import com.jojo.ecommerce.domain.model.UserCoupon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 실제 MySQL에 연결
class UserCouponRepositoryMyBatisDbTest {
    @Autowired
    private UserCouponRepositoryPort couponRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM user_coupon");
    }

    @Test
    void 유저쿠폰_저장() {
        // given
        Long userId = 1L;
        Long couponId = 100L;

        UserCoupon uc = new UserCoupon(userId, couponId);

        // when - 쿠폰 발급
        UserCoupon saved = couponRepository.saveCoupon(uc);

        // then
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getCouponId()).isEqualTo(couponId);
        assertEquals(saved.getUseYn(),"N");

        // and - exists
        assertThat(couponRepository.existsByUserCouponId(userId, couponId)).isTrue();

        // and - find
        UserCoupon found = couponRepository.findByUserCouponId(userId, couponId);
        assertThat(found.getUserId()).isEqualTo(userId);
        assertThat(found.getCouponId()).isEqualTo(couponId);
    }


    @Test
    void 유저쿠폰_사용() {
        // given - 먼저 저장
        Long userId = 200L;
        Long couponId = 200L;
        UserCoupon uc = new UserCoupon(userId, couponId);

        couponRepository.saveCoupon(uc);

        // when - 사용 상태로 업데이트
        uc.useCoupon();
        UserCoupon updated = couponRepository.updateCoupon(uc);

        // then - 업데이트된 필드 검증
        assertEquals(updated.getUseYn(),"Y");
        assertThat(updated.getUpdDate()).isNotNull();
    }

    @Test
    void 유저쿠폰_조회_실패_예외() {
        assertThrows(UserCouponNotFoundException.class,
                () -> couponRepository.findByUserCouponId(999L, 999L));

    }

    @Test
    void 유저쿠폰_업데이트_실패_예외() {
        UserCoupon uc = new UserCoupon(999L, 999L);

        assertThrows(UserCouponNotFoundException.class,
                () -> couponRepository.updateCoupon(uc));

    }
}
