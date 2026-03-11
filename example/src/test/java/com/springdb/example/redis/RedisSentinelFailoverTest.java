package com.springdb.example.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import static org.junit.jupiter.api.Assertions.*;

class RedisSentinelFailoverTest extends AbstractRedisSentinelTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void shouldWorkAfterMasterFailure() throws InterruptedException {
        String key = "test-sentinel-key";
        redisTemplate.opsForValue().set(key, "alive");
        assertEquals("alive", redisTemplate.opsForValue().get(key));

        stopRedisMaster();
        boolean success = false;
        for (int i = 0; i < 15; i++) {
            try {
                redisTemplate.opsForValue().set(key, "recovered");
                if ("recovered".equals(redisTemplate.opsForValue().get(key))) {
                    success = true;
                    break;
                }
            } catch (Exception ignored) {
            }
            Thread.sleep(2000);
        }

        assertTrue(success, "Кластер не зміг відновитися після падіння майстра");
    }
}