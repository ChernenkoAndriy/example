package com.springdb.example.service;

import com.springdb.example.dtos.CarDto;
import com.springdb.example.entities.CarEntity;
import com.springdb.example.mappers.VehicleMapper;
import com.springdb.example.repository.IRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {
    private final IRepository<CarEntity> carRepository;
    private final VehicleMapper vehicleMapper;

    public List<CarDto> getAllCars() {
        return carRepository.findAll().stream()
                .map(vehicleMapper::toCarDto)
                .collect(Collectors.toList());
    }

    public CarDto getCarById(Long id) {
        CarEntity entity = carRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Car not found with id: " + id));
        return vehicleMapper.toCarDto(entity);
    }

    public CarDto saveCar(CarDto carDto) {
        CarEntity entity = vehicleMapper.toCarEntity(carDto);
        CarEntity savedEntity = carRepository.save(entity);
        return vehicleMapper.toCarDto(savedEntity);
    }

    public CarDto updateCar(Long id, CarDto carDtoDetails) {
        if (!carRepository.existsById(id)) {
            throw new NoSuchElementException("Car not found with id: " + id);
        }

        CarEntity entity = vehicleMapper.toCarEntity(carDtoDetails);
        entity.setId(id);
        CarEntity updatedEntity = carRepository.save(entity);
        return vehicleMapper.toCarDto(updatedEntity);
    }

    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new NoSuchElementException("Car not found with id: " + id);
        }
        carRepository.deleteById(id);
    }
}