package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.dto.PointChargeRequest;
import com.jojo.ecommerce.application.exception.ConcurrencyBusyException;
import com.jojo.ecommerce.application.port.in.PointUseCase;
import com.jojo.ecommerce.application.port.out.PointRepositoryPort;
import com.jojo.ecommerce.domain.model.Point;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Primary
@Service
@RequiredArgsConstructor
public class PointServiceRedis implements PointUseCase {
    private final PointRepositoryPort pointRepo;
    private final RedissonClient redisson;

    @Override
    public Point chargePoint(PointChargeRequest req) {
        // 포인트락 생성
        RLock pointLock = redisson.getLock("lock:point:" + req.point().getUserId());

        boolean locked = false;
        //====== 충전로직 =====
        try {
            locked = pointLock.tryLock(2, 30, TimeUnit.SECONDS); // 최대 2초 대기, 30초 리스니

            if (!locked) {
                throw new ConcurrencyBusyException("잠금 획득 실패");
            }
            // 유저 아이디 조회
            Long userId = req.point().getUserId();
            // 요청 아이디 조회(토큰)
            String requestId = req.requestId();
            // 충전할 포인트
            int amount = req.point().getPoint();

            // 포인트 요청내역 저장
            pointRepo.savePointCharge(userId, requestId, amount);
            // 포인트 충전(신규사용자:insert / 기존 사용자: update)
            pointRepo.saveOrUpdatePoint(new Point(userId, amount));

            return pointRepo.findPointByUserId(userId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConcurrencyBusyException("락 대기 중 인터럽트");
        } finally {
            //  락 해제??
            if (locked) {
                try {pointLock.unlock();}
                catch (Exception ignore) {
                }
            }
        }
    }
}
