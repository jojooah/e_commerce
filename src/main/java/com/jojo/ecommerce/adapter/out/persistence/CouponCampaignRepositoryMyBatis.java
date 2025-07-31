package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.exception.CouponNotFoundException;
import com.jojo.ecommerce.application.port.out.CouponCampaignRepositoryPort;
import com.jojo.ecommerce.domain.model.CouponCampaign;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Primary
@Repository
@Transactional
@RequiredArgsConstructor
public class CouponCampaignRepositoryMyBatis implements CouponCampaignRepositoryPort {

    private final CouponCampaignMapper mapper;

    @Override
    public CouponCampaign findByCampaignId(Long campaignId) {
        CouponCampaign campaign = mapper.selectCampaignById(campaignId);
        if (ObjectUtils.isEmpty(campaign)) {
            throw new CouponNotFoundException(campaignId);
        }
        return campaign;
    }

    @Override
    @Transactional
    public CouponCampaign saveCampaign(CouponCampaign campaign) {
        if (campaign.getCampaignId() == null) {
            mapper.insertCampaign(campaign);
        } else {
            mapper.updateCampaign(campaign);
        }
        return mapper.selectCampaignById(campaign.getCampaignId());
    }
}