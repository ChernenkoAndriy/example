package com.springdb.example.transactions;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import com.springdb.example.service.transactions.PropagationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.IllegalTransactionStateException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionPropagationTest extends AbstractIntegrationTest {

    @Autowired
    private PropagationService propagationService;

    @Autowired
    private JpaCarRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testRequiresNewRollbackDoesNotAffectOuter() {
        repository.deleteAll();
        repository.flush();

        CarEntity carOuter = new CarEntity();
        carOuter.setBrand("OuterBrand");
        carOuter.setModel("Model X");

        CarEntity carInner = new CarEntity();
        carInner.setBrand("InnerBrand");
        carInner.setModel("Model Y");

        propagationService.outerRequired(carOuter, carInner, true);
        List<CarEntity> allCars = repository.findAll();
        System.out.println("Brands in DB: " + allCars.stream().map(CarEntity::getBrand).toList());
        assertEquals(1, allCars.size(), "Має залишитися лише 1 запис (Outer)");
        assertEquals("OuterBrand", allCars.get(0).getBrand());
    }

    @Test
    void testMandatoryFailsWithoutTransaction() {
        CarEntity car = new CarEntity();
        car.setBrand("Mandatory");
        assertThrows(IllegalTransactionStateException.class, () ->
                propagationService.mandatoryMethod(car));
    }
}