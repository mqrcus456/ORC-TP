CREATE SCHEMA accounts;
CREATE TABLE accounts.users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
INSERT INTO accounts.users (first_name, last_name, email, active, created_at, updated_at)
VALUES
('Alice', 'Dupont', 'alice.dupont@example.com', true, NOW(), NOW()),
('Bob', 'Martin', 'bob.martin@example.com', true, NOW(), NOW()),
('Charlie', 'Lemoine', 'charlie.lemoine@example.com', true, NOW(), NOW()),
('David', 'Moreau', 'david.moreau@example.com', true, NOW(), NOW());
