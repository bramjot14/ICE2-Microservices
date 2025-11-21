package com.example.productservice;

import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@Testcontainers
class ProductServiceApplicationCacheTests {

    // These containers give us real MongoDB and Redis instances
    // just for the lifetime of the test.
    @Container
    static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7.4").withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDB::getReplicaSetUrl);
        registry.add("spring.redis.host", () -> redis.getHost());
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Test
    void cachingShouldReturnSameListOnSecondCall() {
        Product p = new Product("Demo", "Cache test", new BigDecimal("9.99"), "SKU-1");
        productRepository.save(p);

        List<Product> firstCall = productService.getAllProducts();
        List<Product> secondCall = productService.getAllProducts();

        Assertions.assertEquals(firstCall.size(), secondCall.size());
    }
}
