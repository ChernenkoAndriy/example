package com.springdb.example.service.queries;

import com.springdb.example.entities.CarEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class NamedCarQueries extends CarQueries {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void deleteByCapacity(int cap) {
        em.createNamedQuery("Car.deleteByCapacity")
                .setParameter("cap", cap)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateModelByBrand(String brand, String newModel) {
        em.createNamedQuery("Car.updateModelByBrand")
                .setParameter("brand", brand)
                .setParameter("model", newModel)
                .executeUpdate();
    }

    @Override
    public Integer getMaxCapacity() {
        return em.createNamedQuery("Car.maxCapacity", Integer.class)
                .getSingleResult();
    }

    @Override
    public List<CarEntity> findAllJoined() {
        return em.createNamedQuery("Car.findAllWithJoin", CarEntity.class)
                .getResultList();
    }

    @Override
    public List<CarEntity> findDynamic(String brand, Integer minCapacity) {
        List<CarEntity> results;

        if (brand != null && !brand.isEmpty()) {
            results = em.createNamedQuery("Car.findByBrandDynamic", CarEntity.class)
                    .setParameter("brand", brand)
                    .getResultList();
        } else {
            results = em.createNamedQuery("Car.findAllWithJoin", CarEntity.class)
                    .getResultList();
        }

        if (minCapacity != null) {
            return results.stream()
                    .filter(c -> c.getSeatingCapacity() >= minCapacity)
                    .toList();
        }
        return results;
    }

    @Override
    public List<Object[]> groupByBrandHaving(long min) {
        return em.createNamedQuery("Car.groupByBrandHaving", Object[].class)
                .setParameter("minCount", min)
                .getResultList();
    }
}