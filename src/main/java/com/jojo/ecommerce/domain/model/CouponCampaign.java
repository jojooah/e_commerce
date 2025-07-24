package com.jojo.ecommerce.domain.model;

import com.jojo.ecommerce.application.exception.OutOfCouponException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponCampaign {
    private Long campaignId;
    private final int totalCount;
    private int issuedCount;

    public CouponCampaign(Long campaignId, int totalCount) {
        this.campaignId  = campaignId;
        this.totalCount  = totalCount;
        this.issuedCount = 0;
    }

    // 쿠폰 발행. 남은 수량이 없으면 예외. 동시성을...위해서 synchronized 사용 ㅠ
    public synchronized void issueOne() {
        if (issuedCount >= totalCount) {
            throw new OutOfCouponException(campaignId);
        }
        issuedCount++;
    }

    public int remaining() {
        return totalCount - issuedCount;
    }
}
