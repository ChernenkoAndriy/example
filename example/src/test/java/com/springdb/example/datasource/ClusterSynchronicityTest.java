package com.springdb.example.datasource;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.config.DataSourceContextHolder;
import com.springdb.example.config.DataSourceType;
import com.springdb.example.dtos.CarDto;
import com.springdb.example.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class ClusterSynchronicityTest extends AbstractIntegrationTest {


    @Autowired
    private CarService carService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void shouldVerifyDataSynchronicityBetweenNodes() {
        Long savedId = transactionTemplate.execute(status -> {
            DataSourceContextHolder.setBranchContext(DataSourceType.PRIMARY);
            CarDto newCar = new CarDto();
            newCar.setModel("Cluster-Test");
            newCar.setBrand("Test-Make");
            return carService.saveCar(newCar).getId();
        });

        transactionTemplate.execute(status -> {
            DataSourceContextHolder.setBranchContext(DataSourceType.REPLICA);
            CarDto replicaCar = new CarDto();
            replicaCar.setId(savedId);
            replicaCar.setModel("Cluster-Test");
            replicaCar.setBrand("Test-Make");
            return carService.saveCar(replicaCar);
        });

        DataSourceContextHolder.clear();

        CarDto retrieved = transactionTemplate.execute(status -> {
            DataSourceContextHolder.setBranchContext(DataSourceType.REPLICA);
            return carService.getCarById(savedId);
        });

        assertNotNull(String.valueOf(retrieved), "Car should be found in Replica");
        assertEquals("Cluster-Test", retrieved.getModel());

        DataSourceContextHolder.clear();
    }
}