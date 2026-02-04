package com.springdb.example.service;

import com.springdb.example.entities.CarEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Component
@RequiredArgsConstructor
public class AdvancedQueryService {
    private final EntityManager em;
    private final DSLContext dsl;

    @Transactional
    public void deleteJpql(int capacity) {
        em.createQuery("DELETE FROM CarEntity c WHERE c.seatingCapacity < :cap")
                .setParameter("cap", capacity).executeUpdate();
    }

    @Transactional
    public void deleteNamed(int capacity) {
        em.createNamedQuery("Car.deleteByCapacity").setParameter("cap", capacity).executeUpdate();
    }

    @Transactional
    public void deleteCriteria(int capacity) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<CarEntity> cd = cb.createCriteriaDelete(CarEntity.class);
        Root<CarEntity> root = cd.from(CarEntity.class);
        cd.where(cb.lessThan(root.get("seatingCapacity"), capacity));
        em.createQuery(cd).executeUpdate();
    }

    @Transactional
    public void deleteNative(int capacity) {
        em.createNativeQuery("DELETE FROM car_entity WHERE seating_capacity < ?")
                .setParameter(1, capacity).executeUpdate();
    }

    @Transactional
    public void deleteJooq(int capacity) {
        dsl.deleteFrom(table("car_entity"))
                .where(field("seating_capacity").lt(capacity)).execute();
    }

    public List<CarEntity> joinJpql() {
        return em.createQuery("SELECT c FROM CarEntity c", CarEntity.class).getResultList();
    }

    public List<CarEntity> joinCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CarEntity> cq = cb.createQuery(CarEntity.class);
        cq.from(CarEntity.class);
        return em.createQuery(cq).getResultList();
    }

    public List<CarEntity> joinNative() {
        String sql = "SELECT v.*, c.seating_capacity FROM vehicle_entity v " +
                "JOIN car_entity c ON v.id = c.id";
        return em.createNativeQuery(sql, CarEntity.class).getResultList();
    }

    public Integer maxCapacityCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<CarEntity> root = cq.from(CarEntity.class);
        cq.select(cb.max(root.get("seatingCapacity")));
        return em.createQuery(cq).getSingleResult();
    }

    public Integer maxCapacityJooq() {
        return dsl.select(DSL.max(field("seating_capacity", Integer.class)))
                .from(table("car_entity"))
                .fetchOne(0, Integer.class);
    }

    public List<CarEntity> dynamicCriteria(String brand, String model) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CarEntity> cq = cb.createQuery(CarEntity.class);
        Root<CarEntity> root = cq.from(CarEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if (brand != null) predicates.add(cb.equal(root.get("brand"), brand));
        if (model != null) predicates.add(cb.equal(root.get("model"), model));

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    public List<CarEntity> dynamicJooq(String brand, String model) {
        var conditions = DSL.noCondition();
        if (brand != null) conditions = conditions.and(field("v.brand").eq(brand));
        if (model != null) conditions = conditions.and(field("v.model").eq(model));

        return dsl.select(
                        field("v.id").as("id"),
                        field("v.brand").as("brand"),
                        field("v.model").as("model"),
                        field("c.seating_capacity").as("seatingCapacity")
                )
                .from(table("vehicle_entity").as("v"))
                .join(table("car_entity").as("c")).on(field("v.id").eq(field("c.id")))
                .where(conditions)
                .fetch(record -> {
                    CarEntity car = new CarEntity();
                    car.setId(record.get("id", Long.class));
                    car.setBrand(record.get("brand", String.class));
                    car.setModel(record.get("model", String.class));
                    car.setSeatingCapacity(record.get("seatingCapacity", Integer.class));
                    return car;
                });
    }
}