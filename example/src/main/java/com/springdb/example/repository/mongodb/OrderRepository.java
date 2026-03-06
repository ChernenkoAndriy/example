package com.springdb.example.repository.mongodb;

import com.springdb.example.entities.mongodb.*;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByStatus(String status);

    @Query("{ 'userId' : ?0, 'status' : ?1 }")
    List<Order> findOrdersByUserIdAndStatus(String userId, String status);
}