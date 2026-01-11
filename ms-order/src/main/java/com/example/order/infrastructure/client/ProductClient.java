package com.example.order.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            HttpHeaders headers = createHeadersWithAuth();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            return null;
        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new RuntimeException("Unauthorized to access product service", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Product service unreachable", ex);
        }
    }

    public void decreaseStock(Long productId, int qty) {
        try {
            String url = productServiceUrl + "/" + productId + "/stock?stock=" + qty;
            HttpHeaders headers = createHeadersWithAuth();
            HttpEntity<?> entity = new HttpEntity<>(headers);
            restTemplate.exchange(url, HttpMethod.PATCH, entity, Void.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new RuntimeException("Product not found when decreasing stock", ex);
        } catch (HttpClientErrorException.Unauthorized ex) {
            throw new RuntimeException("Unauthorized to access product service", ex);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to decrease product stock", ex);
        }
    }

    /**
     * Crée les headers HTTP avec le token JWT d'authentification.
     */
    private HttpHeaders createHeadersWithAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Récupérer le token JWT du contexte de sécurité
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            String token = (String) authentication.getCredentials();
            headers.setBearerAuth(token);
        }

        return headers;
    }
}
