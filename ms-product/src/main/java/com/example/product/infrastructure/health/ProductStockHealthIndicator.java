package com.example.product.infrastructure.health;

import com.example.product.domain.repository.ProductRepository;
import org.springframework.boot.actuate.health.*;
import org.springframework.stereotype.Component;

@Component
public class ProductStockHealthIndicator implements HealthIndicator {

    private final ProductRepository repository;

    public ProductStockHealthIndicator(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        long lowStock = repository.countByStockLessThan(5);
        return lowStock > 0
                ? Health.down().withDetail("lowStockProducts", lowStock).build()
                : Health.up().build();
    }
}
