package com.example.order.infrastructure.web.controller;

import com.example.order.application.dto.CreateOrderRequest;
import com.example.order.application.dto.OrderResponse;
import com.example.order.application.dto.OrderItemResponse;
import com.example.order.domain.entity.Order;
import com.example.order.domain.entity.OrderItem;
import com.example.order.domain.entity.OrderStatus;
import com.example.order.application.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@Validated
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> all() {
        List<OrderResponse> orders = service.findAll().stream()
            .map(this::map)
            .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable Long id) {
        Order o = service.findById(id);
        if (o == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(map(o));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest req) {
        Order saved = service.create(req);
        return ResponseEntity.ok(map(saved));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<OrderResponse> changeStatus(@PathVariable Long id, @PathVariable OrderStatus status) {
        Order updated = service.changeStatus(id, status);
        return ResponseEntity.ok(map(updated));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> byUser(@PathVariable Long userId) {
        List<OrderResponse> orders = service.findByUser(userId).stream()
            .map(this::map)
            .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> byStatus(@PathVariable OrderStatus status) {
        List<OrderResponse> orders = service.findByStatus(status).stream()
            .map(this::map)
            .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }


    private OrderResponse map(Order o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setUserId(o.getUserId());
        r.setOrderDate(o.getOrderDate());
        r.setStatus(o.getStatus());
        r.setTotalAmount(o.getTotalAmount());
        r.setShippingAddress(o.getShippingAddress());
        r.setCreatedAt(o.getCreatedAt());
        r.setUpdatedAt(o.getUpdatedAt());
        r.setItems(o.getItems().stream().map(this::mapItem).collect(Collectors.toList()));
        return r;
    }

    private OrderItemResponse mapItem(OrderItem i) {
        OrderItemResponse r = new OrderItemResponse();
        r.setId(i.getId());
        r.setProductId(i.getProductId());
        r.setProductName(i.getProductName());
        r.setQuantity(i.getQuantity());
        r.setUnitPrice(i.getUnitPrice());
        r.setSubtotal(i.getSubtotal());
        return r;
    }
}
