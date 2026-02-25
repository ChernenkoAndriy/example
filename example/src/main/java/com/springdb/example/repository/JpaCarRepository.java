package com.springdb.example.repository;

import com.springdb.example.entities.CarEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@ConditionalOnProperty(name = "app.repository.type", havingValue = "jpa", matchIfMissing = true)
public interface JpaCarRepository extends JpaRepository<CarEntity, Long>, IRepository<CarEntity> {
    Optional<CarEntity> findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long emId);
}
