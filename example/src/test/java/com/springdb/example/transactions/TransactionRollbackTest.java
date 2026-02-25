package com.springdb.example.transactions;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import com.springdb.example.service.transactions.TransactionRollbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class TransactionRollbackTest extends AbstractIntegrationTest {

    @Autowired
    private TransactionRollbackService rollbackService;

    @Autowired
    private JpaCarRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void shouldRollbackForCheckedException() {
        CarEntity car = new CarEntity();
        car.setBrand("Rollback");
        car.setModel("Test");
        assertThrows(Exception.class, () -> rollbackService.saveWithCheckedException(car));
        assertEquals(0, repository.count());
    }

    @Test
    void shouldNotRollbackForRuntimeException() {
        CarEntity car = new CarEntity();
        car.setBrand("NoRollback");
        car.setModel("Test");
        assertThrows(RuntimeException.class, () -> rollbackService.saveWithRuntimeExceptionNoRollback(car));
        assertEquals(1, repository.count());
        assertEquals("NoRollback", repository.findAll().get(0).getBrand());
    }
}