package com.springdb.example.indexes;

import com.springdb.example.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IndexExistenceTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldVerifyThatIndexExistsInDatabase() {
        String sql = "SELECT COUNT(*) FROM pg_indexes " +
                "WHERE tablename = 'vehicle_entity' " +
                "AND indexname = 'idx_vehicle_number'";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

        assertTrue(count > 0, "Index 'idx_vehicle_number' should exist on 'vehicle_entity' table");
    }
}