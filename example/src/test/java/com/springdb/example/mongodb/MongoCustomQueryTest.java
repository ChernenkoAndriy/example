package com.springdb.example.mongodb;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.entities.mongodb.*;
import com.springdb.example.repository.mongodb.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MongoCustomQueryTest extends AbstractIntegrationTest {

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
    void shouldFindEntitiesUsingCustomQueries() {
        User u1 = new User(); u1.setEmail("a@test.com"); u1.setAge(20); userRepository.save(u1);
        User u2 = new User(); u2.setEmail("b@test.com"); u2.setAge(30); userRepository.save(u2);
        User u3 = new User(); u3.setEmail("c@test.com"); u3.setAge(40); userRepository.save(u3);

        List<User> middleAged = userRepository.findUsersByAgeBetween(25, 35);
        assertEquals(1, middleAged.size());
        assertEquals(30, middleAged.get(0).getAge());

        Product p1 = new Product(); p1.setTitle("Mouse"); p1.setPrice(10.0); productRepository.save(p1);
        Product p2 = new Product(); p2.setTitle("Keyboard"); p2.setPrice(50.0); productRepository.save(p2);

        List<Product> cheapProducts = productRepository.findProductsByMaxPrice(20.0);
        assertEquals(1, cheapProducts.size());
        assertEquals("Mouse", cheapProducts.get(0).getTitle());

        Order o1 = new Order(); o1.setUserId("user_1"); o1.setStatus("NEW"); orderRepository.save(o1);
        Order o2 = new Order(); o2.setUserId("user_1"); o2.setStatus("SHIPPED"); orderRepository.save(o2);
        Order o3 = new Order(); o3.setUserId("user_2"); o3.setStatus("NEW"); orderRepository.save(o3);

        List<Order> user1NewOrders = orderRepository.findOrdersByUserIdAndStatus("user_1", "NEW");
        assertEquals(1, user1NewOrders.size());
        assertEquals("user_1", user1NewOrders.get(0).getUserId());
        assertEquals("NEW", user1NewOrders.get(0).getStatus());
    }
}