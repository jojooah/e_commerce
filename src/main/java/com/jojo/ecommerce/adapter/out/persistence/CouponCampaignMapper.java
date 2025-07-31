package com.jojo.ecommerce.adapter.out.persistence;

import com.jojo.ecommerce.domain.model.CouponCampaign;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CouponCampaignMapper {


    /**
     * 쿠폰 캠페인 조회
     * @param campaignId
     * @return
     */
    CouponCampaign selectCampaignById(@Param("campaignId") Long campaignId);

    /**
     * 쿠폰캠페인 저장
     * @param campaign
     * @return
     */
    int insertCampaign(CouponCampaign campaign);

    /**
     * 쿠폰캠페인 수정
     * @param campaign
     * @return
     */
    int updateCampaign(CouponCampaign campaign);
}
