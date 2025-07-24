package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.application.exception.CouponNotFoundException;
import com.jojo.ecommerce.application.port.out.CouponCampaignRepositoryPort;
import com.jojo.ecommerce.application.port.out.CouponRepositoryPort;
import com.jojo.ecommerce.domain.model.Coupon;
import com.jojo.ecommerce.domain.model.CouponCampaign;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CouponCampaignRepositoryMap implements CouponCampaignRepositoryPort{
    private final Map<Long, CouponCampaign> repository = new ConcurrentHashMap<>();
    private Long sequence = 0L;

    @Override
    public CouponCampaign findByCampaignId(Long campaignId) {
        return repository.get(campaignId);
    }

    @Override
    public CouponCampaign saveCampaign(CouponCampaign campaign) {
        if (campaign.getCampaignId() == null) {
            campaign.setCampaignId(++sequence);
        }
        repository.put(campaign.getCampaignId(), campaign);
        return campaign;
    }
}
