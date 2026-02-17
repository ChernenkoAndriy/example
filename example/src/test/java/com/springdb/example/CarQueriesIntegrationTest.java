package com.springdb.example;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import com.springdb.example.service.queries.CarQueries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CarQueriesIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JpaCarRepository jpaCarRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private List<CarQueries> allQueryImplementations;

    private Stream<CarQueries> queryStrategies() {
        return allQueryImplementations.stream();
    }

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

    @ParameterizedTest
    @MethodSource("queryStrategies")
    void shouldVerifyDelete(CarQueries strategy) {
        strategy.deleteByCapacity(3);
        assertEquals(2, jpaCarRepository.count(), "Failed for strategy: " + strategy.getClass().getSimpleName());
    }

    @ParameterizedTest
    @MethodSource("queryStrategies")
    void shouldVerifyUpdate(CarQueries strategy) {
        strategy.updateModelByBrand("Toyota", "Updated Model");
        String actualModel = jdbcTemplate.queryForObject(
                "SELECT model FROM vehicle_entity WHERE brand = 'Toyota'", String.class);
        assertEquals("Updated Model", actualModel, "Failed for strategy: " + strategy.getClass().getSimpleName());
    }

    @ParameterizedTest
    @MethodSource("queryStrategies")
    void shouldVerifyMaxCapacity(CarQueries strategy) {
        assertEquals(5, strategy.getMaxCapacity(), "Failed for strategy: " + strategy.getClass().getSimpleName());
    }

    @ParameterizedTest
    @MethodSource("queryStrategies")
    void shouldVerifyJoin(CarQueries strategy) {
        List<CarEntity> cars = strategy.findAllJoined();
        assertEquals(3, cars.size(), "Failed for strategy: " + strategy.getClass().getSimpleName());
        assertNotNull(cars.get(0).getBrand());
    }

    @ParameterizedTest
    @MethodSource("queryStrategies")
    void shouldVerifyDynamicSearch(CarQueries strategy) {
        List<CarEntity> result = strategy.findDynamic("Toyota", 5);
        assertEquals(1, result.size());
        assertEquals("Camry", result.get(0).getModel());
        assertEquals(1, strategy.findDynamic("Porsche", null).size());
        assertEquals(2, strategy.findDynamic(null, 5).size());
    }

    @ParameterizedTest
    @MethodSource("queryStrategies")
    void shouldVerifyHaving(CarQueries strategy) {
        saveCar("Toyota", "Corolla", 5); // Додаємо ще одну Toyota

        List<Object[]> results = strategy.groupByBrandHaving(1L);

        assertEquals(1, results.size(), "Failed for strategy: " + strategy.getClass().getSimpleName());
        assertEquals("Toyota", results.get(0)[0]);
        // Перевіряємо Count (може бути Long або Integer залежно від стратегії)
        assertTrue(Integer.parseInt(results.get(0)[1].toString()) > 1);
    }

}