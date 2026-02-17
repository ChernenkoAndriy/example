package com.springdb.example;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.CarJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;

import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    void cleanDb() {
        jdbcTemplate.execute("DELETE FROM car_entity");
        jdbcTemplate.execute("DELETE FROM vehicle_entity");
    }

    @Test
    void shouldSaveAndFindAllCars() {
        
        saveSampleCar("Tesla", "Model S", 5);
        saveSampleCar("Audi", "A8", 5);

        
        List<CarEntity> allCars = carJdbcRepository.findAll();

        
        assertEquals(2, allCars.size());
    }

    @Test
    void shouldFindCarById() {
        
        CarEntity saved = saveSampleCar("Toyota", "Camry", 5);

        
        Optional<CarEntity> found = carJdbcRepository.findById(saved.getId());

        
        assertTrue(found.isPresent());
        assertEquals("Toyota", found.get().getBrand());
    }

    @Test
    void shouldUpdateExistingCar() {
        
        CarEntity car = saveSampleCar("Ford", "Focus", 5);
        car.setModel("Mustang");
        car.setSeatingCapacity(4);

        
        carJdbcRepository.save(car); 
        CarEntity updated = carJdbcRepository.findById(car.getId()).orElseThrow();

        
        assertEquals("Mustang", updated.getModel());
        assertEquals(4, updated.getSeatingCapacity());
    }

    @Test
    void shouldDeleteCarById() {
        
        CarEntity car = saveSampleCar("Mazda", "RX-8", 4);
        Long id = car.getId();

        
        carJdbcRepository.deleteById(id);

        
        assertFalse(carJdbcRepository.existsById(id));
    }

    @Test
    void shouldCheckIfCarExists() {
        
        CarEntity car = saveSampleCar("Volvo", "XC90", 7);

        
        assertTrue(carJdbcRepository.existsById(car.getId()));
        assertFalse(carJdbcRepository.existsById(-1L));
    }

    @Test
    void shouldFindByBrand() {
        
        saveSampleCar("BMW", "M3", 4);
        saveSampleCar("BMW", "X5", 5);
        saveSampleCar("Honda", "Civic", 5);

        
        List<CarEntity> bmwCars = carJdbcRepository.findByBrand("BMW");

        
        assertEquals(2, bmwCars.size());
    }

    @Test
    void shouldVerifyJoinedInheritanceViaNativeSql() {
        
        CarEntity car = new CarEntity();
        car.setBrand("Porsche");
        car.setModel("911");
        car.setSeatingCapacity(2);
        CarEntity savedCar = carJdbcRepository.save(car);

        
        Integer vehicleCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vehicle_entity WHERE id = ?", Integer.class, savedCar.getId());
        assertEquals(1, vehicleCount);

        
        Integer carCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM car_entity WHERE id = ?", Integer.class, savedCar.getId());
        assertEquals(1, carCount);
    }

    
    private CarEntity saveSampleCar(String brand, String model, int capacity) {
        CarEntity car = new CarEntity();
        car.setBrand(brand);
        car.setModel(model);
        car.setSeatingCapacity(capacity);
        return carJdbcRepository.save(car);
    }
}