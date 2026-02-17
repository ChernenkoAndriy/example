package com.springdb.example.service.queries;

import com.springdb.example.entities.CarEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Component
@RequiredArgsConstructor
public class JooqCarQueries extends CarQueries {

    private final DSLContext dsl;

    @Override
    @Transactional
    public void deleteByCapacity(int cap) {
        dsl.deleteFrom(table("car_entity"))
                .where(field("seating_capacity").lt(cap))
                .execute();
    }

    @Override
    @Transactional
    public void updateModelByBrand(String brand, String newModel) {
        dsl.update(table("vehicle_entity"))
                .set(field("model"), newModel)
                .where(field("brand").eq(brand))
                .execute();
    }

    @Override
    public Integer getMaxCapacity() {
        return dsl.select(DSL.max(field("seating_capacity", Integer.class)))
                .from(table("car_entity"))
                .fetchOne(0, Integer.class);
    }

    @Override
    public List<CarEntity> findAllJoined() {
        return dsl.select(
                        field("v.id"),
                        field("v.brand"),
                        field("v.model"),
                        field("c.seating_capacity"))
                .from(table("vehicle_entity").as("v"))
                .join(table("car_entity").as("c")).on(field("v.id").eq(field("c.id")))
                .fetch()
                .map(this::mapRecordToCar);
    }

    @Override
    public List<CarEntity> findDynamic(String brand, Integer minCapacity) {
        var condition = DSL.noCondition();
        if (brand != null && !brand.isEmpty()) {
            condition = condition.and(field("v.brand").eq(brand));
        }
        if (minCapacity != null) {
            condition = condition.and(field("c.seating_capacity").ge(minCapacity));
        }

        return dsl.select(
                        field("v.id"),
                        field("v.brand"),
                        field("v.model"),
                        field("c.seating_capacity"))
                .from(table("vehicle_entity").as("v"))
                .join(table("car_entity").as("c")).on(field("v.id").eq(field("c.id")))
                .where(condition)
                .fetch()
                .map(this::mapRecordToCar);
    }

    @Override
    public List<Object[]> groupByBrandHaving(long min) {
        return dsl.select(field("brand"), DSL.count())
                .from(table("vehicle_entity"))
                .groupBy(field("brand"))
                .having(DSL.count().gt((int) min))
                .fetchInto(Object[].class);
    }


    private CarEntity mapRecordToCar(Record record) {
        CarEntity car = new CarEntity();
        car.setId(record.get(field("v.id"), Long.class));
        car.setBrand(record.get(field("v.brand"), String.class));
        car.setModel(record.get(field("v.model"), String.class));
        car.setSeatingCapacity(record.get(field("c.seating_capacity"), Integer.class));
        return car;
    }
}