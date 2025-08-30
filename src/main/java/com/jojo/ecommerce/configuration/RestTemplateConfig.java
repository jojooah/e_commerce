package com.jojo.ecommerce.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder builder,
            @Value("${data-platform.connect-timeout-ms:1500}") int connectTimeoutMs,
            @Value("${data-platform.read-timeout-ms:3000}") int readTimeoutMs
    ) {
        return builder.connectTimeout(Duration.ofMillis(connectTimeoutMs)).readTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }
}
