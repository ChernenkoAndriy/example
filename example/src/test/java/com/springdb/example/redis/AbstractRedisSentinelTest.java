package com.springdb.example.redis;

import com.springdb.example.AbstractIntegrationTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;

public abstract class AbstractRedisSentinelTest extends AbstractIntegrationTest {

    @DynamicPropertySource
    static void redisSentinelProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.sentinel.master", () -> "mymaster");
        registry.add("spring.data.redis.sentinel.nodes", () -> {
            String host = environment.getServiceHost("redis-sentinel", 26379);
            Integer port = environment.getServicePort("redis-sentinel", 26379);
            return String.format("%s:%d", host, port);
        });
    }

    protected void stopRedisMaster() {
        environment.getContainerByServiceName("redis-master-1")
                .ifPresent(container -> {
                    String containerId = container.getContainerId();
                    DockerClientFactory.instance().client()
                            .stopContainerCmd(containerId)
                            .exec();
                });
    }
}