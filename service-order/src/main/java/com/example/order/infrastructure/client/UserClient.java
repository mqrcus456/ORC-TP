package com.example.order.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;

@Component
public class UserClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String userServiceUrl;

    public UserClient(@Value("${order.user-service-url}") String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }

    public boolean exists(Long userId) {
        try {
            String url = userServiceUrl + "/" + userId;
            Map resp = restTemplate.getForObject(url, Map.class);
            return resp != null;
        } catch (HttpClientErrorException.NotFound ex) {
            return false;
        } catch (Exception ex) {
            throw new RuntimeException("User service unreachable", ex);
        }
    }
}
