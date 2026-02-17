package com.springdb.example.service.queries;

import com.springdb.example.entities.CarEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class NativeSqlCarQueries extends CarQueries {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void deleteByCapacity(int cap) {
        em.createNativeQuery("DELETE FROM car_entity WHERE seating_capacity < ?")
                .setParameter(1, cap)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateModelByBrand(String brand, String newModel) {
        em.createNativeQuery("UPDATE vehicle_entity SET model = ? WHERE brand = ?")
                .setParameter(1, newModel)
                .setParameter(2, brand)
                .executeUpdate();
    }

    @Override
    public Integer getMaxCapacity() {
        return (Integer) em.createNativeQuery("SELECT MAX(seating_capacity) FROM car_entity")
                .getSingleResult();
    }

    @Override
    public List<CarEntity> findAllJoined() {
        String sql = "SELECT v.*, c.seating_capacity FROM vehicle_entity v " +
                "JOIN car_entity c ON v.id = c.id";
        return em.createNativeQuery(sql, CarEntity.class).getResultList();
    }

    @Override
    public List<CarEntity> findDynamic(String brand, Integer minCapacity) {
        StringBuilder sql = new StringBuilder(
                "SELECT v.*, c.seating_capacity FROM vehicle_entity v " +
                        "JOIN car_entity c ON v.id = c.id WHERE 1=1"
        );

        if (brand != null && !brand.isEmpty()) {
            sql.append(" AND v.brand = :brand");
        }
        if (minCapacity != null) {
            sql.append(" AND c.seating_capacity >= :minCap");
        }

        Query query = em.createNativeQuery(sql.toString(), CarEntity.class);

        if (brand != null && !brand.isEmpty()) {
            query.setParameter("brand", brand);
        }
        if (minCapacity != null) {
            query.setParameter("minCap", minCapacity);
        }

        return query.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object[]> groupByBrandHaving(long min) {
        String sql = "SELECT brand, COUNT(*) FROM vehicle_entity GROUP BY brand HAVING COUNT(*) > ?";
        return em.createNativeQuery(sql)
                .setParameter(1, min)
                .getResultList();
    }
}