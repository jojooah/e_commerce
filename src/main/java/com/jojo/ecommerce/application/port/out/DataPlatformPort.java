package com.jojo.ecommerce.application.port.out;

import java.util.Map;

public interface DataPlatformPort {
    void sendOrderEvent(Map<String, Object> payload, String idempotencyKey);
}
