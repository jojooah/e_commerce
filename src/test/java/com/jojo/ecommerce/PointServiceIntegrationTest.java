package com.jojo.ecommerce;

import com.jojo.ecommerce.application.dto.CreatePointChargeRequest;
import com.jojo.ecommerce.application.port.out.PointRepositoryPort;
import com.jojo.ecommerce.application.service.PointService;
import com.jojo.ecommerce.domain.model.Point;
import com.sun.jdi.request.DuplicateRequestException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * 성공!
 */
@SpringBootTest
@Transactional
public class PointServiceIntegrationTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private PointRepositoryPort pointRepo;

    private final Long userId = 777L;

    @Test
    void charge_포인트_정상_충전() {

        //500 원 충전요청
        CreatePointChargeRequest createPointChargeRequest = new CreatePointChargeRequest(new Point(userId, 500),"1234");
        Point after = pointService.chargePoint(createPointChargeRequest);

        //  모두 잔액 500
        assertEquals(500, after.getPoint(), "반환된 Point 잔액이 오백원이어야 한다");

        //500 원 충전요청
        CreatePointChargeRequest createPointChargeRequest2 = new CreatePointChargeRequest(new Point(userId, 500),"4567");
         pointService.chargePoint(createPointChargeRequest2);
        Point saved = pointRepo.findPointByUserId(userId);
        assertEquals(1000, saved.getPoint(), "저장소에 반영된 Point 잔액이 1000 한다");
    }

    @Test
    void charge_신규사용자_포인트_충전() {
        // given: 초기화되지 않은 새 사용자 ID
        Long newUserId = 888L;
        CreatePointChargeRequest createPointChargeRequest = new CreatePointChargeRequest(new Point(newUserId, 200),"2345");

        // when: 200 포인트 충전
        Point charged = pointService.chargePoint(createPointChargeRequest);

        // then
        assertEquals(200, charged.getPoint(), "신규 사용자 충전 후 잔액이 200이어야 한다");
        assertEquals(200, pointRepo.findPointByUserId(newUserId).getPoint(),
                "저장소에도 200이 반영되어야 한다");
    }

    @Test
    void 중복해서_요청_불가() {
        // given: 최초 충전 요청
        CreatePointChargeRequest first = new CreatePointChargeRequest(new Point(userId, 300), "12345");
        pointService.chargePoint(first);

        // when & then: 동일 requestId로 재요청 시 예외 발생
        CreatePointChargeRequest duplicate = new CreatePointChargeRequest(new Point(userId, 300), "12345");
        assertThrows(DuplicateRequestException.class,
                () -> pointService.chargePoint(duplicate),
                "동일한 requestId로 중복 충전 들어오면 에외 발생해야 한다");
    }

}
