package com.springdb.example;

import com.springdb.example.config.DataSourceContextHolder;
import com.springdb.example.config.DataSourceType;
import com.springdb.example.dtos.CarDto;
import com.springdb.example.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

class ClusterSynchronicityTest extends AbstractIntegrationTest {

    @Autowired
    private CarService carService;

    @Test
    void shouldVerifyDataSynchronicityBetweenNodes() {
        DataSourceContextHolder.set(DataSourceType.PRIMARY);
        CarDto car = new CarDto();
        car.setBrand("Cluster-Test");
        car.setModel("Sync-Check");
        CarDto saved = carService.saveCar(car);
        DataSourceContextHolder.clear();
        DataSourceContextHolder.set(DataSourceType.REPLICA);
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        CarDto retrieved = carService.getCarById(saved.getId());
        assertEquals("Cluster-Test", retrieved.getBrand());
        DataSourceContextHolder.clear();
    }
}