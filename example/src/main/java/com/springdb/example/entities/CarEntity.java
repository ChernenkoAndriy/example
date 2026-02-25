package com.springdb.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "Car.deleteByCapacity", query = "DELETE FROM CarEntity c WHERE c.seatingCapacity < :cap"),
        @NamedQuery(name = "Car.updateModelByBrand", query = "UPDATE CarEntity c SET c.model = :model WHERE c.brand = :brand"),
        @NamedQuery(name = "Car.findAllWithJoin", query = "SELECT c FROM CarEntity c"),
        @NamedQuery(name = "Car.maxCapacity", query = "SELECT MAX(c.seatingCapacity) FROM CarEntity c"),
        @NamedQuery(name = "Car.findByBrandDynamic", query = "SELECT c FROM CarEntity c WHERE c.brand = :brand"),
        @NamedQuery(name = "Car.groupByBrandHaving", query = "SELECT c.brand, COUNT(c) FROM CarEntity c GROUP BY c.brand HAVING COUNT(c) > :minCount")
})
public class CarEntity extends VehicleEntity {
    private Integer seatingCapacity;
}