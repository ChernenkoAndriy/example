package com.springdb.example.service.transactions;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InnerPropagationService {
    private final JpaCarRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerRequiresNew(CarEntity car, boolean fail) {
        repository.save(car);
        if (fail) {
            throw new RuntimeException("Inner transaction failed");
        }
    }
}