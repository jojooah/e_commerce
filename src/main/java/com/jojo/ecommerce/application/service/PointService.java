package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.adapter.out.persistence.PointMapper;
import com.jojo.ecommerce.application.dto.CreatePointChargeRequest;
import com.jojo.ecommerce.domain.model.Point;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointMapper mapper;

    @Transactional
    public Point chargePoint(CreatePointChargeRequest createPointChargeRequest) {
        Point point = createPointChargeRequest.point();
        point.setRequestId(createPointChargeRequest.RequestId());
        int already = mapper.countPointByUserIdAndRequestId(point);

        // 1) 중복 충전 확인
        if (already > 0) {
            throw new DuplicateRequestException("이미 처리된 요청입니다: ");
        }

        // 2) 기존 포인트 조회
        Long userId = point.getUserId();
        Point saved = mapper.selectPointByUserId(userId);

        if (saved == null) {
            // 신규 사용자: INSERT
            mapper.insertPoint(point);
            return point;

        } else {
            // 기존 사용자: UPDATE
            Point addedPoint = saved.addPoint(point.getPoint());
            mapper.updatePoint(addedPoint);
            return saved;
        }
    }
}
