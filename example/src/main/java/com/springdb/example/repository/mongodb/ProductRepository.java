package com.springdb.example.repository.mongodb;

import com.springdb.example.entities.mongodb.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategory(String category);

    @Query("{ 'price' : { $lte: ?0 } }")
    List<Product> findProductsByMaxPrice(double maxPrice);
}