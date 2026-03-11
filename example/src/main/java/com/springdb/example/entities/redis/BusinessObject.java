package com.springdb.example.entities.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("BusinessObject")
public class BusinessObject implements Serializable {
    @Id
    private String id;
    private String name;
    private String description;
    private String status;
    private Double price;
}