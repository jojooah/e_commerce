package com.jojo.ecommerce.application.service;

import com.jojo.ecommerce.application.port.out.DataPlatformPort;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Repository
@Primary
@RequiredArgsConstructor
public class DataPlatformClientRest implements DataPlatformPort {
    private final RestTemplate restTemplate;

    @Value("${data-platform.base-url}")
    private String baseUrl;

    @Override
    public void sendOrderEvent(Map<String, Object> payload, String idempotencyKey) {
        postJson("/events/order", payload, idempotencyKey);
    }

    private void postJson(String path, Map<String, Object> payload, String idempotencyKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            headers.add("Idempotency-Key", idempotencyKey);
        }
        restTemplate.postForEntity(baseUrl + path, new HttpEntity<>(payload, headers), Void.class);
    }
}
