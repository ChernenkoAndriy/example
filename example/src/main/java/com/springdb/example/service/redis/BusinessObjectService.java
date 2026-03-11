package com.springdb.example.service.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springdb.example.entities.redis.BusinessObject;
import com.springdb.example.repository.redis.BusinessObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BusinessObjectService {

    private final BusinessObjectRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper; // Додано для мапінгу об'єктів
    private static final String HASH_KEY_PREFIX = "BusinessObject:";

    public BusinessObject save(BusinessObject obj) {
        return repository.save(obj);
    }

    public void saveAsHash(BusinessObject obj) {
        String key = HASH_KEY_PREFIX + obj.getId();
        Map<String, String> mappedObject = objectMapper.convertValue(
                obj,
                new TypeReference<Map<String, String>>() {}
        );

        redisTemplate.opsForHash().putAll(key, mappedObject);
    }

    public BusinessObject findByIdFromHash(String id) {
        String key = HASH_KEY_PREFIX + id;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        if (entries.isEmpty()) {
            throw new NoSuchElementException("Object not found in Hash");
        }
        return objectMapper.convertValue(entries, BusinessObject.class);
    }

    public void deleteFromHash(String id) {
        String key = HASH_KEY_PREFIX + id;
        redisTemplate.delete(key);
    }

    public BusinessObject findById(String id) {
        return repository.findById(id).orElseThrow(() -> new NoSuchElementException("Object not found"));
    }

    public void patchUpdate(String id, Map<String, String> patch) {
        String key = HASH_KEY_PREFIX + id;
        redisTemplate.opsForHash().putAll(key, patch);
    }
}