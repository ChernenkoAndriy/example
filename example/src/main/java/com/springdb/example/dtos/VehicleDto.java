package com.springdb.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class VehicleDto {
    private Long id;
    private String brand;
    private String model;
}