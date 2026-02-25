package com.springdb.example.service.transactions;

import com.springdb.example.config.DataSourceContextHolder;
import com.springdb.example.config.DataSourceType;
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

    @Transactional
    public void executeWithDelay(long ms) {
        DataSourceContextHolder.setBranchContext(DataSourceType.PRIMARY);
        repository.count();

        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}