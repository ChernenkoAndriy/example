package com.springdb.example.service.transactions;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@RequiredArgsConstructor
public class ProgrammaticTemplateComponent {
    private final JpaCarRepository repository;
    private final TransactionTemplate transactionTemplate;

    public void transaction1(CarEntity car) {
        transactionTemplate.executeWithoutResult(status -> repository.save(car));
    }

    public void transaction2(Long id, int cap) {
        transactionTemplate.execute(status -> {
            repository.findById(id).ifPresent(c -> c.setSeatingCapacity(cap));
            return null;
        });
    }

    public void transaction3(Long id) {
        transactionTemplate.executeWithoutResult(status -> repository.deleteById(id));
    }
}