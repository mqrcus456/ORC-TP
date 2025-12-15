/*package com.example.order;

import com.example.order.application.service.OrderService;
import com.example.order.application.dto.CreateOrderRequest;
import com.example.order.application.dto.OrderItemRequest;
import com.example.order.domain.entity.Order;
import com.example.order.domain.entity.OrderStatus;
import com.example.order.domain.repository.OrderRepository;
import com.example.order.infrastructure.client.UserClient;
import com.example.order.infrastructure.client.ProductClient;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private MeterRegistry meterRegistry;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, userClient, productClient, meterRegistry);
    }

    @Test
    void testCreateOrder() {
        // Préparer les mocks si nécessaire
        MockitoAnnotations.openMocks(this);

        // Créer les items
        List<OrderItemRequest> items = new ArrayList<>();

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setProductId(101L);
        item1.setQuantity(2);
        items.add(item1);

        OrderItemRequest item2 = new OrderItemRequest();
        item2.setProductId(102L);
        item2.setQuantity(1);
        items.add(item2);

        // Créer la requête
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(2L);
        request.setShippingAddress("123 Main St");
        request.setItems(items);

        // Créer un Order simulé
        Order mockOrder = new Order();
        mockOrder.setUserId(request.getUserId());
        mockOrder.setOrderDate(LocalDateTime.now());
        mockOrder.setStatus(OrderStatus.PENDING);
        mockOrder.setTotalAmount(BigDecimal.valueOf(100));
        mockOrder.setShippingAddress(request.getShippingAddress());

        // Mock du repository
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);

        // Appel du service
        Order created = orderService.create(request);

        // Assertions
        assertNotNull(created);
        assertEquals(OrderStatus.PENDING, created.getStatus());
        assertEquals(BigDecimal.valueOf(100), created.getTotalAmount());
    }

    @Test
    void testChangeOrderStatus() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        Order updated = orderService.changeStatus(1L, OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, updated.getStatus());
    }

    @Test
    void testCancelOrder() {
        Order order = new Order();
        order.setId(2L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Long orderId = 2L;
        
        // Appel de la méthode void
        orderService.cancel(orderId);

        // Vérifier que l'ordre a été mis à jour
        assertEquals(OrderStatus.CANCELLED, order.getStatus());

        // Vérifier que le repository a bien été appelé
        verify(orderRepository).save(order);
    }

}
*/