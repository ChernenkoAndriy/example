package com.springdb.example.redis;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.entities.redis.UserProfile;
import com.springdb.example.service.redis.UserProfileRedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class UserProfileRedisTest extends AbstractRedisSentinelTest {

    @Autowired
    private UserProfileRedisService service;

    @Test
    void shouldPerformCrudOnUserProfile() throws Exception {
        UserProfile profile = new UserProfile("1", "andruf", "test@test.com", 25);

        service.save(profile);

        UserProfile found = service.findById("1");
        assertNotNull(found);
        assertEquals("andruf", found.getUsername());

        found.setAge(26);
        service.save(found);
        assertEquals(26, service.findById("1").getAge());

        service.delete("1");
        assertNull(service.findById("1"));
    }
}