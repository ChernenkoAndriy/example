package com.springdb.example.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private static final String RATE_LIMIT_PREFIX = "rate:limit:user:";

    public boolean isAllowed(String userId, int limit, Duration window) {
        String key = RATE_LIMIT_PREFIX + userId;
        Long currentRequests = redisTemplate.opsForValue().increment(key);

        if (currentRequests != null && currentRequests == 1) {
            redisTemplate.expire(key, window);
        }

        return currentRequests != null && currentRequests <= limit;
    }
}