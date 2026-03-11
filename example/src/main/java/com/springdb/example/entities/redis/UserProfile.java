package com.springdb.example.entities.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    private String id;
    private String username;
    private String email;
    private Integer age;
}