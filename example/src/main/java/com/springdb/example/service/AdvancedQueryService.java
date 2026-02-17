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

    @Transactional public void deleteJpql(int cap) { em.createQuery("DELETE FROM CarEntity c WHERE c.seatingCapacity < :cap").setParameter("cap", cap).executeUpdate(); }
    @Transactional public void deleteNamed(int cap) { em.createNamedQuery("Car.deleteByCapacity").setParameter("cap", cap).executeUpdate(); }
    @Transactional public void deleteCriteria(int cap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<CarEntity> cd = cb.createCriteriaDelete(CarEntity.class);
        Root<CarEntity> root = cd.from(CarEntity.class);
        cd.where(cb.lessThan(root.get("seatingCapacity"), cap));
        em.createQuery(cd).executeUpdate();
    }
    @Transactional public void deleteNative(int cap) { em.createNativeQuery("DELETE FROM car_entity WHERE seating_capacity < ?").setParameter(1, cap).executeUpdate(); }
    @Transactional public void deleteJooq(int cap) { dsl.deleteFrom(table("car_entity")).where(field("seating_capacity").lt(cap)).execute();}

    @Transactional public void updateJpql(String brand, String newModel) { em.createQuery("UPDATE CarEntity c SET c.model = :m WHERE c.brand = :b").setParameter("m", newModel).setParameter("b", brand).executeUpdate(); }
    @Transactional public void updateNamed(String brand, String newModel) { em.createNamedQuery("Car.updateModelByBrand").setParameter("brand", brand).setParameter("model", newModel).executeUpdate(); }
    @Transactional public void updateCriteria(String brand, String newModel) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<CarEntity> cu = cb.createCriteriaUpdate(CarEntity.class);
        Root<CarEntity> root = cu.from(CarEntity.class);
        cu.set("model", newModel).where(cb.equal(root.get("brand"), brand));
        em.createQuery(cu).executeUpdate();
    }
    @Transactional public void updateNative(String brand, String newModel) { em.createNativeQuery("UPDATE vehicle_entity SET model = ? WHERE brand = ?").setParameter(1, newModel).setParameter(2, brand).executeUpdate(); }
    @Transactional public void updateJooq(String brand, String newModel) { dsl.update(table("vehicle_entity")).set(field("model"), newModel).where(field("brand").eq(brand)).execute(); }

    public Integer maxJpql() { return em.createQuery("SELECT MAX(c.seatingCapacity) FROM CarEntity c", Integer.class).getSingleResult(); }
    public Integer maxNamed() { return em.createNamedQuery("Car.maxCapacity", Integer.class).getSingleResult(); }
    public Integer maxCriteria() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        cq.select(cb.max(cq.from(CarEntity.class).get("seatingCapacity")));
        return em.createQuery(cq).getSingleResult();
    }
    public Integer maxNative() { return (Integer) em.createNativeQuery("SELECT MAX(seating_capacity) FROM car_entity").getSingleResult(); }
    public Integer maxJooq() { return dsl.select(DSL.max(field("seating_capacity", Integer.class))).from(table("car_entity")).fetchOne(0, Integer.class); }

    public List<CarEntity> joinJpql() { return em.createQuery("SELECT c FROM CarEntity c", CarEntity.class).getResultList(); }    public List<CarEntity> joinNamed() { return em.createNamedQuery("Car.findAllWithJoin", CarEntity.class).getResultList(); }
    public List<CarEntity> joinCriteria() {
        CriteriaQuery<CarEntity> cq = em.getCriteriaBuilder().createQuery(CarEntity.class);
        cq.from(CarEntity.class);
        return em.createQuery(cq).getResultList();
    }
    public List<CarEntity> joinNative() {
        String sql = "SELECT v.*, c.seating_capacity FROM vehicle_entity v JOIN car_entity c ON v.id = c.id";
        return em.createNativeQuery(sql, CarEntity.class).getResultList();
    }
    public List<CarEntity> joinJooq() {
        return dsl.select(field("v.id"), field("v.brand"), field("v.model"), field("c.seating_capacity"))
                .from(table("vehicle_entity").as("v"))
                .join(table("car_entity").as("c")).on(field("v.id").eq(field("c.id")))
                .fetchInto(CarEntity.class);
    }

    public List<CarEntity> dynamicJpql(String brand) {
        StringBuilder sb = new StringBuilder("SELECT c FROM CarEntity c WHERE 1=1");
        if (brand != null) sb.append(" AND c.brand = :b");
        var q = em.createQuery(sb.toString(), CarEntity.class);
        if (brand != null) q.setParameter("b", brand);
        return q.getResultList();
    }
    public List<CarEntity> dynamicNamed(String brand) { return em.createNamedQuery("Car.findByBrandDynamic", CarEntity.class).setParameter("brand", brand).getResultList(); }
    public List<CarEntity> dynamicCriteria(String brand) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CarEntity> cq = cb.createQuery(CarEntity.class);
        Root<CarEntity> root = cq.from(CarEntity.class);
        List<Predicate> p = new ArrayList<>();
        if (brand != null) p.add(cb.equal(root.get("brand"), brand));
        cq.where(p.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
    public List<CarEntity> dynamicNative(String brand) {
        String sql = "SELECT * FROM vehicle_entity v JOIN car_entity c ON v.id = c.id WHERE 1=1";
        if (brand != null) sql += " AND v.brand = '" + brand + "'";
        return em.createNativeQuery(sql, CarEntity.class).getResultList();
    }
    public List<CarEntity> dynamicJooq(String brand) {
        var cond = DSL.noCondition();
        if (brand != null) cond = cond.and(field("brand").eq(brand));
        return dsl.select().from(table("vehicle_entity")).join(table("car_entity")).on(field("vehicle_entity.id").eq(field("car_entity.id")))
                .where(cond).fetchInto(CarEntity.class);
    }

    public List<Object[]> havingJpql(long min) { return em.createQuery("SELECT c.brand, COUNT(c) FROM CarEntity c GROUP BY c.brand HAVING COUNT(c) > :min", Object[].class).setParameter("min", min).getResultList(); }
    public List<Object[]> havingNamed(long min) { return em.createNamedQuery("Car.groupByBrandHaving", Object[].class).setParameter("minCount", min).getResultList(); }
    public List<Object[]> havingCriteria(long min) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<CarEntity> root = cq.from(CarEntity.class);
        cq.multiselect(root.get("brand"), cb.count(root));
        cq.groupBy(root.get("brand"));
        cq.having(cb.greaterThan(cb.count(root), min));
        return em.createQuery(cq).getResultList();
    }
    public List<Object[]> havingNative(long min) {
        String sql = "SELECT brand, COUNT(*) FROM vehicle_entity GROUP BY brand HAVING COUNT(*) > ?";
        return em.createNativeQuery(sql).setParameter(1, min).getResultList();
    }
    public List<Object[]> havingJooq(long min) {
        return dsl.select(field("brand"), DSL.count())
                .from(table("vehicle_entity"))
                .groupBy(field("brand"))
                .having(DSL.count().gt((int)min))
                .fetchInto(Object[].class);
    }
}