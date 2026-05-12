package com.osoki.paymentsystem.redis.service.impl;

import com.osoki.paymentsystem.payment.dto.PaymentResponse;
import com.osoki.paymentsystem.redis.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private static final Duration TTL = Duration.ofMinutes(10);
    private static final Duration LOCK_TTL = Duration.ofSeconds(30);
    private static final String PREFIX = "payment:idempotency:data:";
    private static final String LOCK_PREFIX = "payment:idempotency:lock:";

    private String buildKey(String key) {
        return PREFIX + key;
    }

    private String buildLockKey(String key) {
        return LOCK_PREFIX + key;
    }
    private final RedisTemplate<String,PaymentResponse> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Optional<PaymentResponse> getCachedResponse(String key) {
        PaymentResponse cachedResponse =
                redisTemplate.opsForValue().get(buildKey(key));

        return Optional.ofNullable(cachedResponse);
    }

    @Override
    public void cacheResponse(String key, PaymentResponse response) {
        redisTemplate.opsForValue()
                .set(buildKey(key), response, TTL);
    }

    @Override
    public boolean acquireLock(String key) {
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(buildLockKey(key), "LOCKED", LOCK_TTL);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void releaseLock(String key) {
        stringRedisTemplate.delete(buildLockKey(key));
    }
}
