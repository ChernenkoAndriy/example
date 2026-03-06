package com.springdb.example.mongodb;

import com.springdb.example.AbstractIntegrationTest;
import com.springdb.example.entities.mongodb.Order;
import com.springdb.example.entities.mongodb.Product;
import com.springdb.example.entities.mongodb.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MongoTemplateTest extends AbstractIntegrationTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void clean() {
        mongoTemplate.dropCollection(User.class);
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Order.class);
    }

    @Test
    void shouldPerformQueriesUsingMongoTemplate() {
        User user = new User();
        user.setName("John Doe");
        user.setAge(25);
        mongoTemplate.save(user);

        Product p1 = new Product();
        p1.setTitle("Cheap Phone");
        p1.setCategory("Electronics");
        p1.setPrice(200.0);
        mongoTemplate.save(p1);

        Product p2 = new Product();
        p2.setTitle("Expensive Phone");
        p2.setCategory("Electronics");
        p2.setPrice(1000.0);
        mongoTemplate.save(p2);

        Order order = new Order();
        order.setUserId(user.getId());
        order.setStatus("PENDING");
        mongoTemplate.save(order);

        Query userQuery = new Query();
        userQuery.addCriteria(Criteria.where("age").gte(20).and("name").is("John Doe"));
        List<User> foundUsers = mongoTemplate.find(userQuery, User.class);

        assertEquals(1, foundUsers.size());
        assertEquals(25, foundUsers.get(0).getAge());

        Query productQuery = new Query();
        productQuery.addCriteria(Criteria.where("category").is("Electronics"));
        productQuery.with(Sort.by(Sort.Direction.DESC, "price"));
        List<Product> sortedProducts = mongoTemplate.find(productQuery, Product.class);

        assertEquals(2, sortedProducts.size());
        assertEquals("Expensive Phone", sortedProducts.get(0).getTitle());
        assertTrue(sortedProducts.get(0).getPrice() > sortedProducts.get(1).getPrice());

        Query updateQuery = new Query();
        updateQuery.addCriteria(Criteria.where("userId").is(user.getId()));

        Update update = new Update();
        update.set("status", "SHIPPED");

        mongoTemplate.updateFirst(updateQuery, update, Order.class);

        Order updatedOrder = mongoTemplate.findById(order.getId(), Order.class);
        assertNotNull(updatedOrder);
        assertEquals("SHIPPED", updatedOrder.getStatus());
    }
}