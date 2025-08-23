package com.jojo.ecommerce.application.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductRankResponse {
    int rank;
    long productId;
    String productName;
    long totalQuantity ;// 누적 판매수량

}