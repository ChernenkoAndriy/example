package com.springdb.example.service.transactions;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionRollbackService {

    private final JpaCarRepository repository;

    @Transactional(rollbackFor = Exception.class)
    public void saveWithCheckedException(CarEntity car) throws Exception {
        repository.save(car);
        throw new Exception("Checked Exception - Transaction should rollback");
    }

    @Transactional(noRollbackFor = RuntimeException.class)
    public void saveWithRuntimeExceptionNoRollback(CarEntity car) {
        repository.save(car);
        throw new RuntimeException("Runtime Exception - Transaction should NOT rollback");
    }
}