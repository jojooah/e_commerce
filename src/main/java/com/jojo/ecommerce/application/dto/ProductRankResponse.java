package com.jojo.ecommerce.application.dto;


public record ProductRankResponse(
        long rank,           // 랭킹
        long productId,
        String productName,
        long totalQuantity // 누적 판매수량

) {
    public static ProductRankResponse of(long rank, long productId, String productName, long totalQuantity) {
        return new ProductRankResponse(rank, productId, productName, totalQuantity);
    }
}
