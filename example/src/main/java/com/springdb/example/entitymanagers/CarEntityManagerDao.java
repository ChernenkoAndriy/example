package com.springdb.example.entitymanagers;

import com.springdb.example.entities.CarEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class CarEntityManagerDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void persist(CarEntity car) {
        entityManager.persist(car);
    }

    public CarEntity find(Long id) {
        return entityManager.find(CarEntity.class, id);
    }

    public void detach(CarEntity car) {
        entityManager.detach(car);
    }

    @Transactional
    public void remove(Long id) {
        CarEntity car = entityManager.find(CarEntity.class, id);
        if (car != null) {
            entityManager.remove(car);
        }
    }

    @Transactional
    public void refresh(CarEntity car) {
        entityManager.refresh(car);
    }

    @Transactional
    public CarEntity merge(CarEntity car) {
        return entityManager.merge(car);
    }
}