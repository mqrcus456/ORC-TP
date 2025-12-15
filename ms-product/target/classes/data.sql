create database productdb;
GRANT ALL PRIVILEGES ON DATABASE productdb TO product_user;

CREATE TYPE productdb.product_category AS ENUM (
    'ELECTRONICS',
    'BOOKS',
    'FOOD',
    'OTHER'
);

CREATE TABLE productdb.product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL CHECK (char_length(name) BETWEEN 3 AND 100),
    description VARCHAR(500) NOT NULL CHECK (char_length(description) BETWEEN 10 AND 500),
    price NUMERIC(10, 2) NOT NULL CHECK (price > 0),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    category product_category NOT NULL,
    image_url VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO product (name, description, price, stock, category, active, created_at, updated_at)
VALUES
('Clavier mécanique', 'Clavier mécanique rétroéclairé avec touches mécaniques', 80.50, 10, 'ELECTRONICS', true, NOW(), NOW()),
('Souris gaming', 'Souris ergonomique pour gamers avec DPI réglable', 20.00, 25, 'ELECTRONICS', true, NOW(), NOW()),
('Casque audio', 'Casque audio sans fil avec réduction de bruit active', 75.00, 15, 'ELECTRONICS', true, NOW(), NOW()),
('Écran 24 pouces', 'Écran LED 24 pouces Full HD avec ports HDMI et VGA', 180.99, 8, 'ELECTRONICS', true, NOW(), NOW()),
('Câble HDMI', 'Câble HDMI 2.1 haute vitesse pour vidéo 4K', 30.00, 50, 'ELECTRONICS', true, NOW(), NOW()),
('Clé USB 64GB', 'Clé USB 3.0 de 64GB pour stockage rapide', 22.60, 100, 'ELECTRONICS', true, NOW(), NOW()),
('Chaise gamer', 'Chaise gamer ergonomique avec accoudoirs réglables', 300.00, 5, 'OTHER', true, NOW(), NOW());