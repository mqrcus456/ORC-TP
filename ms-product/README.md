# Service Order

Microservice to manage orders.

Ports: 8083

Contains:
- Entities Order, OrderItem
- REST API /api/v1/orders
- Calls User service (port 8081) and Product service (port 8082)
- Actuator health and Prometheus metrics
