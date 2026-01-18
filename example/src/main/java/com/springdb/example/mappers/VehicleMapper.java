package com.springdb.example.mappers;

import com.springdb.example.dtos.CarDto;
import com.springdb.example.dtos.TruckDto;
import com.springdb.example.entities.CarEntity;
import com.springdb.example.entities.TruckEntity;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public CarDto toCarDto(CarEntity entity) {
        if (entity == null) return null;
        CarDto dto = new CarDto();
        dto.setId(entity.getId());
        dto.setBrand(entity.getBrand());
        dto.setModel(entity.getModel());
        dto.setSeatingCapacity(entity.getSeatingCapacity());
        return dto;
    }

    public CarEntity toCarEntity(CarDto dto) {
        if (dto == null) return null;
        CarEntity entity = new CarEntity();
        entity.setId(dto.getId());
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setSeatingCapacity(dto.getSeatingCapacity());
        return entity;
    }

    public TruckDto toTruckDto(TruckEntity entity) {
        if (entity == null) return null;
        TruckDto dto = new TruckDto();
        dto.setId(entity.getId());
        dto.setBrand(entity.getBrand());
        dto.setModel(entity.getModel());
        dto.setPayloadCapacity(entity.getPayloadCapacity());
        return dto;
    }

    public TruckEntity toTruckEntity(TruckDto dto) {
        if (dto == null) return null;
        TruckEntity entity = new TruckEntity();
        entity.setId(dto.getId());
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setPayloadCapacity(dto.getPayloadCapacity());
        return entity;
    }
}
