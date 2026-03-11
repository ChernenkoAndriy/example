package com.springdb.example.repository.redis;

import com.springdb.example.entities.redis.BusinessObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessObjectRepository extends CrudRepository<BusinessObject, String> {
}