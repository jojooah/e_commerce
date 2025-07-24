package com.jojo.ecommerce;

import com.jojo.ecommerce.application.port.out.PointRepositoryPort;
import com.jojo.ecommerce.application.service.PointService;
import com.jojo.ecommerce.domain.model.Point;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class PointServiceIntegrationTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepositoryPort pointRepo;

    private final Long userId = 777L;

    @BeforeEach
    void setUp() {
        pointRepo.updatePoint(new Point(userId, 1000));
    }

    @Test
    void charge_포인트_정상_충전() {
        // given: 현재 잔액 1000
        Point before = pointRepo.findPointByUserId(userId);
        assertEquals(1000, before.getPoint(), "초기 포인트는 1000이어야 한다");

        // when: 500원 충전
        Point after = pointService.chargePoint(new Point(userId, 500));

        //  모두 잔액 1500
        assertEquals(1500, after.getPoint(), "반환된 Point 잔액이 1500이어야 한다");
        Point persisted = pointRepo.findPointByUserId(userId);
        assertEquals(1500, persisted.getPoint(), "저장소에 반영된 Point 잔액이 1500이어야 한다");
    }

    @Test
    void charge_신규사용자_포인트_충전() {
        // given: 초기화되지 않은 새 사용자 ID
        Long newUserId = 888L;
        Point initial = pointRepo.findPointByUserId(newUserId);
        assertEquals(0, initial.getPoint(), "신규 사용자는 초기 포인트 0이어야 한다");

        // when: 200 포인트 충전
        Point charged = pointService.chargePoint(new Point(newUserId, 200));

        // then
        assertEquals(200, charged.getPoint(), "신규 사용자 충전 후 잔액이 200이어야 한다");
        assertEquals(200, pointRepo.findPointByUserId(newUserId).getPoint(),
                "저장소에도 200이 반영되어야 한다");
    }
}
