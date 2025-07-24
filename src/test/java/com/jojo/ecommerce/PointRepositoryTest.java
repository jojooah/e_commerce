package com.jojo.ecommerce;

import com.jojo.ecommerce.adapter.out.persistence.PointRepositoryMap;
import com.jojo.ecommerce.application.exception.InsufficientPointException;
import com.jojo.ecommerce.domain.model.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 포인트 충전 테스트
 */
public class PointRepositoryTest {
    private PointRepositoryMap repo;

    @BeforeEach
    void setUp() {
        repo = new PointRepositoryMap();
    }

    @Test
    void 유저가_포인트를_충전한적이_없으면_0원() {
        Long userId = 123L;
        Point p = repo.findPointByUserId(userId);

        assertNotNull(p, "Point 객체는 null이 아니어야 한다");
        assertEquals(userId, p.getUserId(), "userId가 그대로 설정되어야 한다");
    }

    @Test
    void 잔액보다_더_많이_사용하면_예외(){
        Point point = new Point(1L, 1000);
        Point saved = repo.updatePoint(point);

        assertThrows(
                InsufficientPointException.class,
                () -> saved.minusPoint(1500),
                "잔액보다 큰 금액 차감 시 InsufficientPointException이 발생해야 한다"
        );
    }

    @Test
    void 천원에서_오백원_충전하면_천오백원(){
        //  천원 있음
        Point point = new Point(1L,1000);
        assertEquals(1000, point.getPoint());
        // 오백원 충전
        point.addPoint(500);
        Point saved = repo.updatePoint(point);

        //천오백원
        assertEquals(1500, saved.getPoint());
    }

}
