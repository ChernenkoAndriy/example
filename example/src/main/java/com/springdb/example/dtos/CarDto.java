package com.springdb.example.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CarDto extends VehicleDto {
    private Integer seatingCapacity;
}