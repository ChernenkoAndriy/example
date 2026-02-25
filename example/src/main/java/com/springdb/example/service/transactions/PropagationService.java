package com.springdb.example.service.transactions;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PropagationService {
    private final JpaCarRepository repository;
    private final InnerPropagationService innerService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void outerRequired(CarEntity car1, CarEntity car2, boolean failInner) {
        repository.save(car1);
        try {
            innerService.innerRequiresNew(car2, failInner);
        } catch (RuntimeException ignored) {
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void mandatoryMethod(CarEntity car) {
        repository.save(car);
    }
}