package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.exception.AlreadyIssuedException;
import com.jojo.ecommerce.application.port.out.CouponCampaignRepositoryPort;
import com.jojo.ecommerce.application.port.out.UserCouponRepositoryPort;
import com.jojo.ecommerce.domain.model.CouponCampaign;
import com.jojo.ecommerce.domain.model.UserCoupon;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueCouponService {
    private final CouponCampaignRepositoryPort campaignRepo;
    private final UserCouponRepositoryPort userCouponRepo;

   // 캠페인별로 선착순 쿠폰 발행
    @Transactional
    public UserCoupon issue(Long campaignId, Long userId) {
        // 중복 발급 체크
        if (userCouponRepo.existsByUserCouponId(userId,campaignId)) {
            throw new AlreadyIssuedException(userId, campaignId);
        }
        //  발행 카운트 증가
        CouponCampaign campaign = campaignRepo.findByCampaignId(campaignId);
        campaign.issueOne();
        campaignRepo.saveCampaign(campaign);

        // 쿠폰 생성
        UserCoupon userCoupon = new UserCoupon(userId, campaignId);
        return userCouponRepo.saveCoupon(userCoupon);
    }
}
