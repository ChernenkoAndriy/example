package com.springdb.example.mongodb;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.entities.mongodb.*;
import com.springdb.example.repository.mongodb.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MongoNamingStrategyTest extends AbstractIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private OrderRepository orderRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void shouldFindEntitiesByNameMethods() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Ivan");
        userRepository.save(user);

        Product product = new Product();
        product.setTitle("Laptop");
        product.setCategory("Electronics");
        productRepository.save(product);

        Order order = new Order();
        order.setStatus("NEW");
        order.setUserId(user.getId());
        orderRepository.save(order);


        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("Ivan", foundUser.get().getName());

        List<Product> electronics = productRepository.findByCategory("Electronics");
        assertEquals(1, electronics.size());
        assertEquals("Laptop", electronics.get(0).getTitle());

        List<Order> newOrders = orderRepository.findByStatus("NEW");
        assertEquals(1, newOrders.size());
        assertEquals(user.getId(), newOrders.get(0).getUserId());
    }
}