package com.jojo.ecommerce;

import com.jojo.ecommerce.application.dto.PaymentCompleteEvent;
import com.jojo.ecommerce.application.service.DataPlatformClientRest;
import com.jojo.ecommerce.application.service.DataPlatformEventListener;
import com.jojo.ecommerce.configuration.RestTemplateConfig;
import com.jojo.ecommerce.domain.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
@SpringBootTest(classes = {
        RestTemplateConfig.class,
        DataPlatformClientRest.class,
        DataPlatformEventListener.class,
        DataPlatformEventListenerIT.AsyncTestConfig.class
})
class DataPlatformEventListenerIT {

    @Configuration
    @EnableAsync
    static class AsyncTestConfig {
        @Bean(name = "applicationEventMulticaster")
        public ApplicationEventMulticaster multicaster() {
            var exec = new ThreadPoolTaskExecutor();
            exec.setCorePoolSize(2);
            exec.setMaxPoolSize(4);
            exec.initialize();
            var m = new org.springframework.context.event.SimpleApplicationEventMulticaster();
            m.setTaskExecutor(exec);
            return m;
        }
    }

    @Autowired RestTemplate restTemplate;
    @Autowired org.springframework.context.ApplicationEventPublisher publisher;

    MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
        server.expect(requestTo("http://localhost:9090/mock-dp/events/order"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Idempotency-Key", "REQ-xyz"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("\"type\":\"PAYMENT_COMPLETED\"")))
                .andRespond(withSuccess());
    }

    @Test
    void 결제완료_이벤트가_트랜잭션없어도_실행된다_fallbackExecution() {
        // 트랜잭션 없이 발행 → fallbackExecution=true 덕분에 즉시 실행
        publisher.publishEvent(new PaymentCompleteEvent(
                111L, 222L, 333L, "REQ-xyz", 9999,
                List.of(new OrderItem(111L, 10L, 1, 1000)),
                Instant.now()
        ));

        // @Async 비동기 완료까지 짧게 대기하며 검증
        AssertionError last = null;
        for (int i = 0; i < 30; i++) {
            try { server.verify(); last = null; break; }
            catch (AssertionError e) { last = e; try { Thread.sleep(100); } catch (InterruptedException ignored) {} }
        }
        if (last != null) throw last;
    }
}
