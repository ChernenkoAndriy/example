package com.springdb.example.web;

import com.springdb.example.dtos.CarDto;
import com.springdb.example.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @GetMapping
    public List<CarDto> getAll() {
        return carService.getAllCars();
    }

    @PostMapping
    public CarDto create(@RequestBody CarDto carDto) {
        return carService.saveCar(carDto);
    }

    @GetMapping("/{id}")
    public CarDto getById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @PutMapping("/{id}")
    public CarDto update(@PathVariable Long id, @RequestBody CarDto carDto) {
        return carService.updateCar(id, carDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}