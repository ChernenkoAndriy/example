package com.springdb.example;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.File;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    static final DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("docker-compose.yaml"))
                    .withExposedService("db-primary", 5432)
                    .withExposedService("db-replica", 5432);
    static {
        environment.start();
    }
}