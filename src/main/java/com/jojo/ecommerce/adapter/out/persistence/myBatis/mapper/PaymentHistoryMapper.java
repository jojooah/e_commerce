package com.jojo.ecommerce.adapter.out.persistence.myBatis.mapper;


import com.jojo.ecommerce.domain.model.PaymentHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentHistoryMapper {
    /**
     * 결제이력 저장
     * @param history
     * @return
     */
    int insertPaymentHistory(PaymentHistory history);

    /**
     * 결제이력 조회
     * @param paymentId
     * @return
     */
    List<PaymentHistory> selectByPaymentId(@Param("paymentId") Long paymentId);
}
