package com.springdb.example;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.CarJdbcRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CarPersistanceTest extends AbstractIntegrationTest {

    @Autowired
    private CarJdbcRepository carJdbcRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void useJdbc(DynamicPropertyRegistry registry) {
        registry.add("app.repository.type", () -> "jdbc");
    }

    @Test
    void shouldVerifyJoinedInheritanceViaNativeSql() {
        CarEntity car = new CarEntity();
        car.setBrand("Porsche");
        car.setModel("911");
        car.setSeatingCapacity(2);
        carJdbcRepository.save(car);

        Map<String, Object> vehicleRow = jdbcTemplate.queryForMap(
                "SELECT * FROM vehicle_entity WHERE brand = 'Porsche'");
        assertNotNull(vehicleRow.get("id"));

        Map<String, Object> carRow = jdbcTemplate.queryForMap(
                "SELECT * FROM car_entity WHERE id = ?", vehicleRow.get("id"));
        assertEquals(2, carRow.get("seating_capacity"));
    }

    @Test
    void shouldVerifyJdbcSpecificQueries() {
        CarEntity car = new CarEntity();
        car.setBrand("BMW");
        car.setModel("M5");
        car.setSeatingCapacity(5);
        carJdbcRepository.save(car);

        List<CarEntity> found = carJdbcRepository.findByBrand("BMW");
        assertFalse(found.isEmpty());
        assertEquals("M5", found.get(0).getModel());
    }
}