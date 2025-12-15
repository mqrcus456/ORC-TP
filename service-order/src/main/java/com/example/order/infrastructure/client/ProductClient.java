package com.example.order.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Component
public class ProductClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String productServiceUrl;

    public ProductClient(@Value("${order.product-service-url}") String productServiceUrl) {
        this.productServiceUrl = productServiceUrl;
    }

    public Map getProduct(Long productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            return restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (Exception ex) {
            throw new RuntimeException("Product service unreachable", ex);
        }
    }

    public void decreaseStock(Long productId, int qty) {
        try {
            String url = productServiceUrl + "/" + productId + "/stock";
            restTemplate.patchForObject(url, Map.of("quantity", qty), Void.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Product not found when decreasing stock", ex);
        }
    }
}
