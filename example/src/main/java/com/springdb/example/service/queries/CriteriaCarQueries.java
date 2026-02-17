package com.springdb.example.service.queries;

import com.springdb.example.entities.CarEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class CriteriaCarQueries extends CarQueries {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void deleteByCapacity(int cap) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaDelete<CarEntity> cd = cb.createCriteriaDelete(CarEntity.class);
        Root<CarEntity> root = cd.from(CarEntity.class);
        cd.where(cb.lessThan(root.get("seatingCapacity"), cap));
        em.createQuery(cd).executeUpdate();
    }

    @Override
    @Transactional
    public void updateModelByBrand(String brand, String newModel) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<CarEntity> cu = cb.createCriteriaUpdate(CarEntity.class);
        Root<CarEntity> root = cu.from(CarEntity.class);
        cu.set("model", newModel);
        cu.where(cb.equal(root.get("brand"), brand));
        em.createQuery(cu).executeUpdate();
    }

    @Override
    public Integer getMaxCapacity() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<CarEntity> root = cq.from(CarEntity.class);
        cq.select(cb.max(root.get("seatingCapacity")));
        return em.createQuery(cq).getSingleResult();
    }

    @Override
    public List<CarEntity> findAllJoined() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CarEntity> cq = cb.createQuery(CarEntity.class);
        cq.from(CarEntity.class);
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<CarEntity> findDynamic(String brand, Integer minCapacity) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CarEntity> cq = cb.createQuery(CarEntity.class);
        Root<CarEntity> root = cq.from(CarEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if (brand != null && !brand.isEmpty()) {
            predicates.add(cb.equal(root.get("brand"), brand));
        }
        if (minCapacity != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("seatingCapacity"), minCapacity));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<Object[]> groupByBrandHaving(long min) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<CarEntity> root = cq.from(CarEntity.class);
        cq.multiselect(root.get("brand"), cb.count(root));
        cq.groupBy(root.get("brand"));
        cq.having(cb.greaterThan(cb.count(root), min));

        return em.createQuery(cq).getResultList();
    }
}