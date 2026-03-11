package com.springdb.example.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdb.example.entities.redis.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileRedisService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "user:profile:";

    public void save(UserProfile profile) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(profile);
        redisTemplate.opsForValue().set(KEY_PREFIX + profile.getId(), json);
    }

    public UserProfile findById(String id) throws JsonProcessingException {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + id);
        if (json == null) return null;
        return objectMapper.readValue(json, UserProfile.class);
    }

    public void delete(String id) {
        redisTemplate.delete(KEY_PREFIX + id);
    }
}