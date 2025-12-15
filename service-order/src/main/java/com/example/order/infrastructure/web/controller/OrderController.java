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
    public ResponseEntity<?> all() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Order o = service.findById(id);
        if (o == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(map(o));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateOrderRequest req) {
        var saved = service.create(req);
        return ResponseEntity.ok(map(saved));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> changeStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        var updated = service.changeStatus(id, status);
        return ResponseEntity.ok(map(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> byUser(@PathVariable Long userId) {
        return ResponseEntity.ok(service.findByUser(userId).stream().map(this::map).collect(Collectors.toList()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> byStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(service.findByStatus(status).stream().map(this::map).collect(Collectors.toList()));
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
