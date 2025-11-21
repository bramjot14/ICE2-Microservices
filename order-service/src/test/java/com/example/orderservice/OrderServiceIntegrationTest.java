package com.example.orderservice;

import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@SpringBootTest
@Testcontainers
class OrderServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("orderdb")
                    .withUsername("order")
                    .withPassword("order");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrderService orderService;

    // We mock RestTemplate so the test focuses on database + service logic.
    @MockBean
    private RestTemplate restTemplate;

    @Test
    void shouldPlaceOrderWhenInventorySaysInStock() {
        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(Boolean.class)))
                .thenReturn(true);

        OrderRequest request = new OrderRequest();
        request.setSkuCode("SKU-1");
        request.setQuantity(2);
        request.setPrice(new BigDecimal("19.99"));

        Order order = orderService.placeOrder(request);

        Assertions.assertNotNull(order.getId());
    }
}
