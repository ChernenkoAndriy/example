package com.springdb.example.datasource;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.config.DataSourceContextHolder;
import com.springdb.example.config.DataSourceType;
import com.springdb.example.service.transactions.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HikariPoolExhaustionTest extends AbstractIntegrationTest {
    @Autowired
    private TransactionHelperService transactionHelperService;

    @Test
    void shouldThrowExceptionWhenHikariPoolIsExhausted() {
        int poolSize = 10;
        ExecutorService executor = Executors.newFixedThreadPool(poolSize + 5);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < poolSize; i++) {
                futures.add(CompletableFuture.runAsync(() -> {
                    DataSourceContextHolder.setBranchContext(DataSourceType.PRIMARY);
                    transactionHelperService.executeWithDelay(10000);
                }, executor));
            }

            Thread.sleep(500);
            assertThrows(ExecutionException.class, () -> {
                CompletableFuture.runAsync(() -> {
                    DataSourceContextHolder.setBranchContext(DataSourceType.PRIMARY);
                    transactionHelperService.executeWithDelay(100);
                }, executor).get();
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdownNow();
            DataSourceContextHolder.clear();
        }
    }
}