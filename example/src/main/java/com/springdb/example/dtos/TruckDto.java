package com.springdb.example.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TruckDto extends VehicleDto {
    private Double payloadCapacity;
}