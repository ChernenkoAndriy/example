package com.springdb.example;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import com.springdb.example.service.AdvancedQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdvancedQueryServiceTest extends AbstractIntegrationTest {

    @Autowired
    private AdvancedQueryService advancedQueryService;

    @Autowired
    private JpaCarRepository jpaCarRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM car_entity");
        jdbcTemplate.execute("DELETE FROM vehicle_entity");

        saveCar("Toyota", "Camry", 5);
        saveCar("Honda", "Civic", 5);
        saveCar("Porsche", "911", 2);
    }

    private void saveCar(String brand, String model, int capacity) {
        CarEntity car = new CarEntity();
        car.setBrand(brand);
        car.setModel(model);
        car.setSeatingCapacity(capacity);
        jpaCarRepository.save(car);
    }

    @Test
    void shouldTestAllDeleteMethods() {
        advancedQueryService.deleteJpql(3);
        assertEquals(2, jpaCarRepository.count());

        advancedQueryService.deleteNamed(10);
        assertEquals(0, jpaCarRepository.count());

        setUp();

        advancedQueryService.deleteCriteria(3);
        assertEquals(2, jpaCarRepository.count());

        advancedQueryService.deleteNative(10);
        assertEquals(0, jpaCarRepository.count());

        setUp();

        advancedQueryService.deleteJooq(3);
        assertEquals(2, jpaCarRepository.count());

    }

    @Test
    void shouldTestJoinMethods() {
        List<CarEntity> joinJpql = advancedQueryService.joinJpql();
        assertFalse(joinJpql.isEmpty());
        assertNotNull(joinJpql.get(0).getBrand());

        List<CarEntity> joinNative = advancedQueryService.joinNative();
        assertEquals(3, joinNative.size());
        assertTrue(joinNative.stream().anyMatch(c -> c.getBrand().equals("Porsche")));

        List<CarEntity> joinCriteria = advancedQueryService.joinCriteria();
        assertEquals(3, joinCriteria.size());
    }

    @Test
    void shouldTestAggregationMethods() {
        Integer maxCrit = advancedQueryService.maxCapacityCriteria();
        assertEquals(5, maxCrit);

        Integer maxJooq = advancedQueryService.maxCapacityJooq();
        assertEquals(5, maxJooq);
    }

    @Test
    void shouldTestDynamicQueries() {
        List<CarEntity> byBrand = advancedQueryService.dynamicCriteria("Toyota", null);
        assertEquals(1, byBrand.size());
        assertEquals("Camry", byBrand.get(0).getModel());

        List<CarEntity> byBoth = advancedQueryService.dynamicCriteria("Porsche", "911");
        assertEquals(1, byBoth.size());

        List<CarEntity> jooqDynamic = advancedQueryService.dynamicJooq("Honda", null);
        assertEquals(1, jooqDynamic.size());
        assertEquals("Civic", jooqDynamic.get(0).getModel());
    }
}