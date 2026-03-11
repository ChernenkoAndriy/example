package com.springdb.example.redis;

import com.springdb.example.entities.redis.BusinessObject;
import com.springdb.example.service.redis.BusinessObjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class BusinessObjectHashTest extends AbstractRedisSentinelTest {

    @Autowired
    private BusinessObjectService service;

    @Test
    void shouldPerformFullCrudUsingHashOperations() {
        BusinessObject obj = new BusinessObject();
        obj.setId("HASH_TEST_1");
        obj.setName("Manual Hash Object");
        obj.setStatus("ACTIVE");
        obj.setPrice(500.0);

        service.saveAsHash(obj);

        BusinessObject found = service.findByIdFromHash("HASH_TEST_1");
        assertNotNull(found);
        assertEquals("Manual Hash Object", found.getName());
        assertEquals(500.0, found.getPrice());

        service.deleteFromHash("HASH_TEST_1");
        assertThrows(NoSuchElementException.class, () -> service.findByIdFromHash("HASH_TEST_1"));
    }

    @Test
    void shouldPerformPartialUpdateUsingHash() {
        BusinessObject obj = new BusinessObject();
        obj.setId("BO1");
        obj.setName("Initial Name");
        obj.setStatus("NEW");
        obj.setPrice(100.0);
        service.save(obj);

        Map<String, String> updates = Map.of(
                "name", "Updated Name",
                "status", "ACTIVE"
        );
        service.patchUpdate("BO1", updates);

        BusinessObject updated = service.findById("BO1");
        assertEquals("Updated Name", updated.getName());
        assertEquals("ACTIVE", updated.getStatus());
        assertEquals(100.0, updated.getPrice());
    }
}