package com.springdb.example.service.transactions;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class TransactionHelperService {

    private final JpaCarRepository repository;

    @Transactional
    public boolean externalTransactionalMethod(CarEntity car) {
        repository.save(car);
        return TransactionSynchronizationManager.isActualTransactionActive();
    }
}