package com.jojo.ecommerce.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 사용자 요청 객체
 */
@Setter
@Getter
public class CreatePaymentRequest {
   private Long userId;
   private Long couponId;
   private Long orderId;
   private List<ProductDto> productDtoList;
   private String paymentMethod;
   private int paymentPrice;

   public CreatePaymentRequest(Long userId, Long couponId,Long orderId, List<ProductDto> productDtoList, String paymentMethod, int paymentPrice) {
      this.userId = userId;
      this.couponId = couponId;
      this.orderId = orderId;
      this.productDtoList = productDtoList;
      this.paymentMethod = paymentMethod;
      this.paymentPrice = paymentPrice;
   }

}

