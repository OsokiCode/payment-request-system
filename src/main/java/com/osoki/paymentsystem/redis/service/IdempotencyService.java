package com.osoki.paymentsystem.redis.service;

import com.osoki.paymentsystem.payment.dto.PaymentResponse;

import java.util.Optional;

public interface IdempotencyService {

    Optional<PaymentResponse> getCachedResponse(String key);

    void cacheResponse(String key, PaymentResponse response);

    boolean acquireLock(String key);

    void releaseLock(String key);
}
