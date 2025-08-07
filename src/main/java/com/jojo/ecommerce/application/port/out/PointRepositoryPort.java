package com.jojo.ecommerce.application.port.out;

import com.jojo.ecommerce.domain.model.Point;

public interface PointRepositoryPort {
    // 포인트 충전
    Point updatePoint(Point point);

    // 유저 포인트 조회
    Point findPointByUserId(Long userId);

    // 유저 포인트 조회 중복확인
    int findPointByUserIdAndRequestId(Long userId,String requestId);

    // 유저 포인트 저장
    void savePoint(Point point);

}
