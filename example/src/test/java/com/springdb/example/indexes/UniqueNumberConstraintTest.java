package com.springdb.example;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UniqueNumberConstraintTest extends AbstractIntegrationTest {

    @Autowired
    private JpaCarRepository repository;

    @Test
    void shouldThrowExceptionWhenDuplicateNumberIsSaved() {
        String duplicateNumber = "AA 1257 ІХ";

        CarEntity car1 = new CarEntity();
        car1.setNumber(duplicateNumber);
        car1.setBrand("Toyota");
        car1.setModel("Camry");
        car1.setSeatingCapacity(5);
        repository.saveAndFlush(car1);

        CarEntity car2 = new CarEntity();
        car2.setNumber(duplicateNumber);
        car2.setBrand("Honda");
        car2.setModel("Civic");
        car2.setSeatingCapacity(5);

        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.saveAndFlush(car2);
        }, "Exception should be thrown when trying to save a car with a duplicate number");
    }
}