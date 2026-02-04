package com.springdb.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(
                name = "Car.deleteByCapacity",
                query = "DELETE FROM CarEntity c WHERE c.seatingCapacity < :cap"
        )
})
public class CarEntity extends VehicleEntity {
    private Integer seatingCapacity;
}