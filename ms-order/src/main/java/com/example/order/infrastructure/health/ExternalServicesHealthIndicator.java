package com.example.order.infrastructure.health;

import com.example.order.infrastructure.client.ProductClient;
import com.example.order.infrastructure.client.UserClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("externalServices")
public class ExternalServicesHealthIndicator implements HealthIndicator {

    private final UserClient userClient;
    private final ProductClient productClient;

    public ExternalServicesHealthIndicator(UserClient userClient, ProductClient productClient){
        this.userClient = userClient;
        this.productClient = productClient;
    }

    @Override
    public Health health() {
        try {
            boolean u = userClient.exists(1L); // a simple ping; implementations treat unreachable as exception
            boolean p = productClient.getProduct(1L) != null;
            if(u && p) return Health.up().withDetail("userService", "up").withDetail("productService","up").build();
            return Health.down().withDetail("userService", u ? "up" : "not-found").withDetail("productService", p ? "found" : "not-found").build();
        } catch (Exception ex){
            return Health.down(ex).build();
        }
    }
}
