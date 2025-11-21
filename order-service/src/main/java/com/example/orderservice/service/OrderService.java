package com.example.orderservice.service;

import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderRequest;
import com.example.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    // Base URL for the API Gateway. Inside Docker this resolves to the
    // api-gateway container name.
    private final String inventoryBaseUrl;

    public OrderService(
            OrderRepository orderRepository,
            RestTemplate restTemplate,
            @Value("${app.inventory-url}") String inventoryBaseUrl) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.inventoryBaseUrl = inventoryBaseUrl;
    }

    public Order placeOrder(OrderRequest request) {
        String url = inventoryBaseUrl + "/api/inventory?skuCode=" + request.getSkuCode();

        Boolean inStock = restTemplate.getForObject(url, Boolean.class);

        if (Boolean.FALSE.equals(inStock)) {
            // Simple validation error. In a real system you would build
            // a nicer response type or use a custom exception.
            throw new IllegalStateException("Product is not in stock");
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setSkuCode(request.getSkuCode());
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());

        return orderRepository.save(order);
    }
}
