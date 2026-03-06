package com.springdb.example;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    static final DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("docker-compose.yaml"))
                    .withExposedService("db-primary", 5432, Wait.forListeningPort())
                    .withExposedService("db-replica", 5432, Wait.forListeningPort())
                    .withExposedService("mongodb", 27017,
                            Wait.forLogMessage(".*Waiting for connections.*\\n", 1));

    static {
        environment.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.docker.compose.enabled", () -> "false");

        registry.add("spring.datasource.primary.hikari.jdbc-url", () -> {
            String host = environment.getServiceHost("db-primary", 5432);
            Integer port = environment.getServicePort("db-primary", 5432);
            return String.format("jdbc:postgresql://%s:%d/sezdb", host, port);
        });

        registry.add("spring.datasource.replica.hikari.jdbc-url", () -> {
            String host = environment.getServiceHost("db-replica", 5432);
            Integer port = environment.getServicePort("db-replica", 5432);
            return String.format("jdbc:postgresql://%s:%d/sezdb", host, port);
        });

        registry.add("spring.data.mongodb.uri", () -> {
            String host = environment.getServiceHost("mongodb", 27017);
            Integer port = environment.getServicePort("mongodb", 27017);
            return String.format("mongodb://admin:password@%s:%d/example_db?authSource=admin", host, port);
        });
    }
}