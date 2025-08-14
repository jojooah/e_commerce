package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.CreatePointChargeRequest;
import com.jojo.ecommerce.application.port.in.PointUseCase;
import com.jojo.ecommerce.application.port.out.PointRepositoryPort;
import com.jojo.ecommerce.domain.model.Point;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService implements PointUseCase {
    private final PointRepositoryPort pointRepo;

    @Transactional
    @Override
    public Point chargePoint(CreatePointChargeRequest req) {
        Long userId = req.point().getUserId();
        int amount = req.point().getPoint();
        String requestId = req.requestId();

        try {
            // (user_id, request_id) UNIQUE
            // 요청내역 우선 저장
            pointRepo.savePointCharge(userId, requestId, amount);
        } catch (DuplicateKeyException e) {
            throw new DuplicateRequestException("이미 처리된 요청입니다:");
        }

        // 원자적 증가
        pointRepo.saveOrUpdatePoint(new Point(userId, amount));
        return pointRepo.findPointByUserId(userId);
    }
}
