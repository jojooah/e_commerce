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
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Long userId = 777L;

    @BeforeEach
    void cleanTables() {
        jdbcTemplate.execute("TRUNCATE TABLE point_charge");
        jdbcTemplate.execute("TRUNCATE TABLE point");
    }

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

    @Test
    void 동시성_중복_requestId_한번만_성공() throws Exception     {
        // given
        int threads = 20;
        int amount = 300;
        String sameRequestId = "1234";

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        AtomicInteger success = new AtomicInteger();
        AtomicInteger duplicateFails = new AtomicInteger();
        List<Throwable> unexpected = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    pointService.chargePoint(new CreatePointChargeRequest(new Point(userId, amount), sameRequestId));
                    success.incrementAndGet();
                } catch (DuplicateRequestException e) {
                    duplicateFails.incrementAndGet();
                } catch (Throwable t) {
                    unexpected.add(t);
                } finally {
                    done.countDown();
                }
            });
        }

        // 모든 스레드 준비될 때까지 대기 후 동시에 시작
        ready.await(5, TimeUnit.SECONDS);
        start.countDown();
        done.await(10, TimeUnit.SECONDS);
        executor.shutdownNow();

        // then

        assertThat(success.get()).as("중복 requestId는 단 1건만 성공").isEqualTo(1);
        assertThat(duplicateFails.get()).as("나머지는 DuplicateRequestException 발생").isEqualTo(threads - 1);

        Point saved = pointRepo.findPointByUserId(userId);
        assertThat(saved).isNotNull();
        assertThat(saved.getPoint()).as("최종 잔액은 한 번만 반영되어야 함").isEqualTo(amount);
    }

    @Test
    void 동시성_서로다른_requestId_모두_성공하고_잔액누적() throws Exception {
        // given
        int threads = 30;
        int amount = 100;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        List<Throwable> unexpected = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threads; i++) {
            final int idx = i;
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    String requestId = "REQ-" + idx; // 모두 다른 requestId
                    pointService.chargePoint(new CreatePointChargeRequest(new Point(userId, amount), requestId));
                } catch (Throwable t) {
                    unexpected.add(t);
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();
        done.await(10, TimeUnit.SECONDS);
        executor.shutdownNow();

        // then
        Point saved = pointRepo.findPointByUserId(userId);
        assertThat(saved).isNotNull();
        assertThat(saved.getPoint()).as("모든 충전이 누적되어야 함").isEqualTo(threads * amount);
    }

}
