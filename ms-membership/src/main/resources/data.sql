CREATE SCHEMA accounts;
CREATE TABLE accounts.users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
INSERT INTO accounts.users (first_name, last_name, email, password, active, created_at, updated_at)
VALUES
('Alice', 'Dupont', 'alice.dupont@example.com', 'password123', true, NOW(), NOW()),
('Bob', 'Martin', 'bob.martin@example.com', 'password123', true, NOW(), NOW()),
('Charlie', 'Lemoine', 'charlie.lemoine@example.com', 'password123', true, NOW(), NOW()),
('David', 'Moreau', 'david.moreau@example.com', 'password123', true, NOW(), NOW());
