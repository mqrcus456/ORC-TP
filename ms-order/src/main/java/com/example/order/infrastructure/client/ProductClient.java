package com.example.order.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Component
public class ProductClient {

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductClient(@Value("${order.product-service-url}") String productServiceUrl) {
        // Important : permet d’utiliser PATCH
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
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
            String url = productServiceUrl + "/" + productId + "/stock?stock=" + qty;
            restTemplate.exchange(url, HttpMethod.PATCH, null, Void.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Product not found when decreasing stock", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to decrease product stock", ex);
        }
    }
}
