package com.springdb.example.repository;

import com.springdb.example.entities.CarEntity;
import java.util.List;
import java.util.Optional;

public interface IRepository<E> {
    List<E> findAll();
    Optional<E> findById(Long id);
    E save(E car);
    void deleteById(Long id);
    boolean existsById(Long id);
}