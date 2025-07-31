package com.jojo.ecommerce;

import com.jojo.ecommerce.application.exception.AlreadyIssuedException;
import com.jojo.ecommerce.application.exception.OutOfCouponException;
import com.jojo.ecommerce.application.port.out.CouponCampaignRepositoryPort;
import com.jojo.ecommerce.application.port.out.UserCouponRepositoryPort;
import com.jojo.ecommerce.application.service.IssueCouponService;
import com.jojo.ecommerce.domain.model.CouponCampaign;
import com.jojo.ecommerce.domain.model.UserCoupon;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class IssueCouponServiceIntegrationTest {
    @Autowired private IssueCouponService service;
    @Autowired private CouponCampaignRepositoryPort campaignRepo;
    @Autowired private UserCouponRepositoryPort ucRepo;
    @Autowired JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        // 캠페인 초기화: 총 2장 발행 가능
        jdbc.update("DELETE FROM user_coupon");
        jdbc.update("DELETE FROM coupon_campaign");
        jdbc.update("ALTER TABLE coupon_campaign AUTO_INCREMENT = 1");

        campaignRepo.saveCampaign(new CouponCampaign(null, 2));
    }

    @Test
    void 쿠폰_2개까지_발행성공() {
        // 1, 2번은 성공
        UserCoupon uc1 = service.issue(1L, 101L);
        UserCoupon uc2 = service.issue(1L, 102L);
        assertNotNull(uc1);
        assertNotNull(uc2);

        // 남은 수량 0
        assertEquals(0, campaignRepo.findByCampaignId(1L).remaining());

        // 3번 발행 시 예외
        assertThrows(OutOfCouponException.class,
                () -> service.issue(1L, 103L));
    }

    @Test
    void 동일_사용자_중복발행하면_예외() {
        service.issue(1L, 201L);
        assertThrows(AlreadyIssuedException.class,
                () -> service.issue(1L, 201L));
    }
}
