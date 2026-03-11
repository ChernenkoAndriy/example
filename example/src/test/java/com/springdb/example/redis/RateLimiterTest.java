package com.springdb.example.redis;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.service.redis.RateLimiterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterTest extends AbstractRedisSentinelTest {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Test
    void shouldLimitRequestsAfterExceedingThreshold() {
        String userId = "user123";
        int limit = 3;
        Duration window = Duration.ofSeconds(10);

        assertTrue(rateLimiterService.isAllowed(userId, limit, window));
        assertTrue(rateLimiterService.isAllowed(userId, limit, window));
        assertTrue(rateLimiterService.isAllowed(userId, limit, window));

        assertFalse(rateLimiterService.isAllowed(userId, limit, window),
                "Fourth request should be blocked after exceeding the limit");
    }
}