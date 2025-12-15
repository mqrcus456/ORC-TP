package com.example.order.application.service;

import com.example.order.application.dto.CreateOrderRequest;
import com.example.order.application.dto.OrderItemRequest;
import com.example.order.domain.entity.Order;
import com.example.order.domain.entity.OrderItem;
import com.example.order.domain.entity.OrderStatus;
import com.example.order.domain.repository.OrderRepository;
import com.example.order.infrastructure.client.ProductClient;
import com.example.order.infrastructure.client.UserClient;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Service class for handling order-related business logic.
 * Provides methods for creating, retrieving, updating, and canceling orders.
 * Includes metrics for monitoring order creation and daily totals.
 */
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;
    private final Counter orderCreatedCounter;
    private final AtomicReference<BigDecimal> dailyOrderTotal;

    /**
     * Constructor for OrderService.
     * Initializes the service with required dependencies and registers metrics.
     *
     * @param orderRepository the repository for order data access
     * @param userClient      client for user service interactions
     * @param productClient   client for product service interactions
     * @param meterRegistry   registry for metrics
     */
    public OrderService(OrderRepository orderRepository, UserClient userClient, ProductClient productClient,
            MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.userClient = userClient;
        this.productClient = productClient;
        this.orderCreatedCounter = Counter.builder("orders.created")
                .tag("status", "PENDING")
                .register(meterRegistry);
        this.dailyOrderTotal = new AtomicReference<>(BigDecimal.ZERO);
        Gauge.builder("orders.daily.total", dailyOrderTotal, ref -> ref.get().doubleValue())
                .register(meterRegistry);
    }

    /**
     * Retrieves all orders.
     *
     * @return list of all orders
     */
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param id the order ID
     * @return the order if found, null otherwise
     */
    public Order findById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    /**
     * Retrieves orders by user ID.
     *
     * @param userId the user ID
     * @return list of orders for the user
     */
    public List<Order> findByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    /**
     * Retrieves orders by status.
     *
     * @param status the order status
     * @return list of orders with the specified status
     */
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Creates a new order based on the provided request.
     * Validates user existence, order items, product availability, and stock.
     * Updates product stock after successful order creation.
     *
     * @param request the create order request
     * @return the created order
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public Order create(CreateOrderRequest request) {
        // Validate user existence
        if (!userClient.exists(request.getUserId())) {
            throw new IllegalArgumentException("User does not exist: " + request.getUserId());
        }

        // Validate order items
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        // Create order entity
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process each order item
        for (OrderItemRequest itemRequest : request.getItems()) {
            Map<String, Object> product = productClient.getProduct(itemRequest.getProductId());
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + itemRequest.getProductId());
            }

            // Check stock availability
            int stock = ((Number) product.getOrDefault("stock", 0)).intValue();
            if (stock < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + itemRequest.getProductId());
            }

            // Parse price
            BigDecimal price = new BigDecimal(product.get("price").toString());

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(itemRequest.getProductId());
            orderItem.setProductName(product.getOrDefault("name", "").toString());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(price);
            orderItem.setSubtotal(price.multiply(BigDecimal.valueOf(itemRequest.getQuantity())));

            order.addItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);

        // Save the order
        Order savedOrder = orderRepository.save(order);

        // Decrease product stock
        for (OrderItem item : savedOrder.getItems()) {
            productClient.decreaseStock(item.getProductId(), item.getQuantity());
        }

        // Update metrics
        orderCreatedCounter.increment();
        dailyOrderTotal.updateAndGet(current -> current
                .add(savedOrder.getTotalAmount() != null ? savedOrder.getTotalAmount() : BigDecimal.ZERO));

        return savedOrder;
    }

    /**
     * Changes the status of an existing order.
     *
     * @param id        the order ID
     * @param newStatus the new status
     * @return the updated order
     * @throws IllegalArgumentException if order not found
     * @throws IllegalStateException    if order cannot be modified
     */
    @Transactional
    public Order changeStatus(Long id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot modify delivered or cancelled order");
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    /**
     * Cancels an order by setting its status to CANCELLED.
     *
     * @param id the order ID
     * @throws IllegalArgumentException if order not found
     * @throws IllegalStateException    if order cannot be cancelled
     */
    @Transactional
    public void cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel delivered or cancelled order");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        // TODO: optionally restore product stock - omitted for brevity
    }
}
