package com.springdb.example.service.transactions;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeclarativeTransactionComponent {
    private final JpaCarRepository repository;

    @Transactional
    public void transaction1(CarEntity car) { repository.save(car); }

    @Transactional
    public void transaction2(Long id, int cap) {
        repository.findById(id).ifPresent(c -> c.setSeatingCapacity(cap));
    }

    @Transactional
    public void transaction3(Long id) { repository.deleteById(id); }
}