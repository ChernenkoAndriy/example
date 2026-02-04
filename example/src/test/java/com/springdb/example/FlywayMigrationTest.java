package com.springdb.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlywayMigrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldPassValidationAndHaveDataInEntities() {
        // Перевіряємо vehicle_entity
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vehicle_entity", Integer.class);
        assertTrue(count > 0, "Дані мали бути перенесені міграціями Flyway");

        // Перевіряємо car_entity (тепер там теж є дані завдяки оновленому V3)
        Integer carCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM car_entity", Integer.class);
        assertTrue(carCount > 0, "Таблиця car_entity має бути заповнена для підтримки JOINED стратегії");
    }
}