package com.springdb.example.service.transactions;

import com.springdb.example.entities.CarEntity;
import com.springdb.example.repository.JpaCarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IsolationService {

    private final JpaCarRepository repository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateCapacitySerializable(Long id, int increment) {
        Optional<CarEntity> carOpt = repository.findById(id);
        if (carOpt.isPresent()) {
            CarEntity car = carOpt.get();
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            car.setSeatingCapacity(car.getSeatingCapacity() + increment);
            repository.save(car);
        }
    }
}