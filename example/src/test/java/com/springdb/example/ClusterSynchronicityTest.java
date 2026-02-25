package com.springdb.example;

import com.springdb.example.config.DataSourceContextHolder;
import com.springdb.example.config.DataSourceType;
import com.springdb.example.dtos.CarDto;
import com.springdb.example.service.CarService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import static com.springdb.example.config.DataSourceType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class ClusterSynchronicityTest extends AbstractIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CarService carService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void shouldVerifyDataSynchronicityBetweenNodes() {
        Long savedId = transactionTemplate.execute(status -> {
            DataSourceContextHolder.setBranchContext(PRIMARY);
            CarDto newCar = new CarDto();
            newCar.setModel("Cluster-Test");
            newCar.setBrand("Test-Make");
            return carService.saveCar(newCar).getId();
        });
        DataSourceContextHolder.clear();
        CarDto retrieved = transactionTemplate.execute(status -> {
            DataSourceContextHolder.setBranchContext(REPLICA);
            return carService.getCarById(savedId);
        });
        assertNotNull(retrieved);
        assertEquals("Cluster-Test", retrieved.getModel());
        DataSourceContextHolder.clear();
    }
}