package com.jojo.ecommerce;

import com.jojo.ecommerce.application.exception.OutOfCouponException;
import com.jojo.ecommerce.application.port.out.CouponCampaignRepositoryPort;
import com.jojo.ecommerce.domain.model.CouponCampaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CouponCampaignRepositoryMyBatisDbTest {

    @Autowired
    private CouponCampaignRepositoryPort campaignRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanUp() {
        jdbcTemplate.update("DELETE FROM coupon_campaign");
    }

    @Test
    void 신규캠페인_저장() {
        // given: 총 5개 발행 가능 캠페인
        CouponCampaign campaign = new CouponCampaign(null, 5);

        // when
        CouponCampaign saved = campaignRepository.saveCampaign(campaign);

        // then
        assertNotNull(saved.getCampaignId(), "campaignId가 null이어서는 안 됩니다");
        assertEquals(5, saved.getTotalCount(), "totalCount 값이 일치해야");
        assertEquals(0, saved.getIssuedCount(), "issuedCount는 0이어야 합니다");

    }


    @Test
    void 발급초과시_예외() {
        // given
        CouponCampaign campaign = new CouponCampaign(null, 1);
        campaignRepository.saveCampaign(campaign);

        // issueOne 두 번 호출하면 두 번째에서 예외
        campaign.issueOne();
        campaignRepository.saveCampaign(campaign);

        assertThrows(OutOfCouponException.class, () -> {
            campaign.issueOne();
        });
    }
}
