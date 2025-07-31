package com.jojo.ecommerce.application.port.out;

import com.jojo.ecommerce.domain.model.CouponCampaign;

public interface CouponCampaignRepositoryPort {
    // 쿠폰캠페인 조회
    CouponCampaign findByCampaignId(Long mapaignId);

    //저장
    CouponCampaign saveCampaign(CouponCampaign campaign);
}
