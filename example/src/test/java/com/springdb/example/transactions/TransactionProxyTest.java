package com.springdb.example.transactions;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.entities.CarEntity;
import com.springdb.example.service.transactions.TransactionProxyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class TransactionProxyTest extends AbstractIntegrationTest {

    @Autowired
    private TransactionProxyService proxyService;

    @Test
    void shouldShowThatInternalCallDoesNotStartTransaction() {
        CarEntity car = new CarEntity();
        car.setBrand("Internal");
        boolean isTransactionActive = proxyService.internalCall(car);
        assertFalse(isTransactionActive, "Транзакція не повинна бути активною при внутрішньому виклику");
    }

    @Test
    void shouldShowThatExternalCallStartsTransaction() {
        CarEntity car = new CarEntity();
        car.setBrand("External");
        boolean isTransactionActive = proxyService.externalCall(car);
        assertTrue(isTransactionActive, "Транзакція має бути активною при виклику зовнішнього сервісу");
    }
}