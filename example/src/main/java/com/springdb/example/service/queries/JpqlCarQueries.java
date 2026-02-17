package com.springdb.example.service.queries;

import com.springdb.example.entities.CarEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JpqlCarQueries extends CarQueries {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void deleteByCapacity(int cap) {
        em.createQuery("DELETE FROM CarEntity c WHERE c.seatingCapacity < :cap")
                .setParameter("cap", cap)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void updateModelByBrand(String brand, String newModel) {
        em.createQuery("UPDATE CarEntity c SET c.model = :model WHERE c.brand = :brand")
                .setParameter("model", newModel)
                .setParameter("brand", brand)
                .executeUpdate();
    }

    @Override
    public Integer getMaxCapacity() {
        return em.createQuery("SELECT MAX(c.seatingCapacity) FROM CarEntity c", Integer.class)
                .getSingleResult();
    }

    @Override
    public List<CarEntity> findAllJoined() {
        return em.createQuery("SELECT c FROM CarEntity c", CarEntity.class)
                .getResultList();
    }

    @Override
    public List<CarEntity> findDynamic(String brand, Integer minCapacity) {
        StringBuilder jpql = new StringBuilder("SELECT c FROM CarEntity c WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();
        if (brand != null && !brand.isEmpty()) {
            jpql.append(" AND c.brand = :brand");
            parameters.put("brand", brand);
        }
        if (minCapacity != null) {
            jpql.append(" AND c.seatingCapacity >= :minCap");
            parameters.put("minCap", minCapacity);
        }
        TypedQuery<CarEntity> query = em.createQuery(jpql.toString(), CarEntity.class);
        parameters.forEach(query::setParameter);

        return query.getResultList();
    }

    @Override
    public List<Object[]> groupByBrandHaving(long min) {
        return em.createQuery(
                        "SELECT c.brand, COUNT(c) FROM CarEntity c GROUP BY c.brand HAVING COUNT(c) > :min",
                        Object[].class)
                .setParameter("min", min)
                .getResultList();
    }
}