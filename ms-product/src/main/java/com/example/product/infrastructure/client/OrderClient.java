package com.example.product.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

@Component
public class OrderClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String orderServiceUrl;

    public OrderClient(@Value("${product.order-service-url}") String orderServiceUrl) {
        this.orderServiceUrl = orderServiceUrl;
    }

    /**
     * Vérifie si le produit est présent dans une commande
     */
    public boolean isProductInOrder(Long productId) {
        try {
            // Appel GET /api/v1/orders
            String url = orderServiceUrl + "/api/v1/orders";
            List<Map> orders = restTemplate.getForObject(url, List.class);

            if (orders == null) return false;

            // Parcours tous les OrderItems
            for (Map order : orders) {
                List<Map> items = (List<Map>) order.get("items");
                if (items != null) {
                    for (Map item : items) {
                        if (productId.equals(Long.valueOf(item.get("productId").toString()))) {
                            return true;
                        }
                    }
                }
            }
            return false;

        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (Exception ex) {
            throw new RuntimeException("Order service unreachable", ex);
        }
    }
}
