package com.springdb.example.service.queries;

import com.springdb.example.entities.CarEntity;
import java.util.List;

public abstract class CarQueries {
    public abstract void deleteByCapacity(int cap);
    public abstract void updateModelByBrand(String brand, String newModel);
    public abstract Integer getMaxCapacity();
    public abstract List<CarEntity> findAllJoined();
    public abstract List<CarEntity> findDynamic(String brand, Integer minCapacity);
    public abstract List<Object[]> groupByBrandHaving(long min);
}