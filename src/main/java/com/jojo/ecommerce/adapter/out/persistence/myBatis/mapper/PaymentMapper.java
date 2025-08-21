package com.jojo.ecommerce.adapter.out.persistence.myBatis.mapper;

import com.jojo.ecommerce.domain.model.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {

    /**
     * 결제정보 조회
     * @param paymentId
     * @return
     */
    Payment selectPaymentById(@Param("paymentId") Long paymentId);

    /**
     * 결제정보 저장
     * @param payment
     * @return
     */
    int insertPayment(Payment payment);

    /**
     * 결제정보 수정
     * @param payment
     * @return
     */
    int updatePayment(Payment payment);
}
