
CREATE SCHEMA shop;

CREATE TYPE shop.order_status AS ENUM ('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED');

CREATE TABLE shop.orders (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT NOW(),
    status shop.order_status NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) NOT NULL,
    shipping_address VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE shop.order_items (
    id SERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES shop.orders(id) ON DELETE CASCADE,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity INT NOT NULL CHECK(quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL
);

INSERT INTO shop.orders (user_id, status, total_amount, shipping_address)
VALUES
(1, 'PENDING',   120.50, '10 rue de Paris, 75001 Paris'),
(2, 'CONFIRMED', 75.00,  '25 avenue Victor Hugo, 69002 Lyon'),
(1, 'SHIPPED',   210.99, '5 boulevard de la République, 13001 Marseille'),
(3, 'DELIVERED', 45.20,  '12 rue Nationale, 59000 Lille'),
(4, 'CANCELLED', 300.00, '8 place Bellecour, 69002 Lyon');

INSERT INTO shop.order_items (order_id, product_id, product_name, quantity, unit_price, subtotal)
VALUES
-- Order 1
(1, 101, 'Clavier mécanique', 1, 80.50, 80.50),
(1, 102, 'Souris gaming',     2, 20.00, 40.00),
-- Order 2
(2, 103, 'Casque audio',      1, 75.00, 75.00),
-- Order 3
(3, 104, 'Écran 24 pouces',   1, 180.99, 180.99),
(3, 105, 'Câble HDMI',        1, 30.00, 30.00),
-- Order 4
(4, 106, 'Clé USB 64GB',      2, 22.60, 45.20),
-- Order 5
(5, 107, 'Chaise gamer',      1, 300.00, 300.00);
