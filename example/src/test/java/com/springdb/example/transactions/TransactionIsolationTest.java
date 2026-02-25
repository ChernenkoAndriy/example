package com.springdb.example.transactions;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import com.springdb.example.service.transactions.IsolationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionIsolationTest extends AbstractIntegrationTest {

    @Autowired
    private IsolationService isolationService;

    @Autowired
    private JpaCarRepository repository;

    @Test
    void shouldThrowExceptionWhenSerializableConflictOccurs() throws Exception {
        CarEntity car = new CarEntity();
        car.setBrand("Toyota");
        car.setSeatingCapacity(5);
        CarEntity saved = repository.saveAndFlush(car);
        Long id = saved.getId();
        CompletableFuture<Void> thread1 = CompletableFuture.runAsync(() ->
                isolationService.updateCapacitySerializable(id, 1));

        CompletableFuture<Void> thread2 = CompletableFuture.runAsync(() ->
                isolationService.updateCapacitySerializable(id, 2));
        ExecutionException exception = assertThrows(ExecutionException.class, () -> {
            CompletableFuture.allOf(thread1, thread2).get();
        });
        System.out.println("Конфлікт серіалізації зафіксовано: " + exception.getCause().getMessage());
    }
}