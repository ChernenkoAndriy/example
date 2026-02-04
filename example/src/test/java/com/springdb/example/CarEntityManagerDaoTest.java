package com.springdb.example;

import com.springdb.example.entities.CarEntity;

import com.springdb.example.entitymanagers.CarEntityManagerDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

class CarEntityManagerDaoTest extends AbstractIntegrationTest {

    @Autowired
    private CarEntityManagerDao carDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldPersistAndFindCar() {
        CarEntity car = new CarEntity();
        car.setBrand("Audi");
        car.setModel("A6");
        car.setSeatingCapacity(5);

        carDao.persist(car);
        assertNotNull(car.getId());

        CarEntity found = carDao.find(car.getId());
        assertEquals("Audi", found.getBrand());
    }

    @Test
    void shouldRemoveCar() {
        CarEntity car = new CarEntity();
        car.setBrand("Mazda");
        carDao.persist(car);
        Long id = car.getId();

        carDao.remove(id);

        CarEntity found = carDao.find(id);
        assertNull(found);
    }

    @Test
    @Transactional
    void shouldDetachCar() {
        CarEntity car = new CarEntity();
        car.setBrand("Volvo");
        carDao.persist(car);

        carDao.detach(car);

        car.setBrand("Changed");

        CarEntity inDb = carDao.find(car.getId());
        assertEquals("Volvo", inDb.getBrand());
    }

    @Test
    @Transactional
    void shouldRefreshCar() {
        CarEntity car = new CarEntity();
        car.setBrand("Ford");
        carDao.persist(car);

        car.setBrand("Temporary Change");

        carDao.refresh(car);

        assertEquals("Ford", car.getBrand());
    }

    @Test
    void shouldMergeCar() {
        CarEntity car = new CarEntity();
        car.setBrand("Nissan");
        carDao.persist(car);

        carDao.detach(car);
        car.setBrand("Nissan Updated");

        CarEntity merged = carDao.merge(car);

        CarEntity inDb = carDao.find(car.getId());
        assertEquals("Nissan Updated", inDb.getBrand());
    }
}