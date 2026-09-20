-- RibinaMart Seed Data Script
-- Passwords:
-- Admin: admin@ribinamart.com / Admin@123
-- Sellers: seller1@ribinamart.com / Seller@123, seller2@ribinamart.com / Seller@123
-- Buyers: buyer1@ribinamart.com / Buyer@123, buyer2@ribinamart.com / Buyer@123

-- Note: Seed data is also dynamically initialized and bcrypt-hashed by DBContextListener on startup.
-- This file provides reference statements.

-- Users
INSERT INTO users (name, email, password_hash, role)
SELECT 'Ribina Admin', 'admin@ribinamart.com', '$2a$12$K8Kq8N94JqgO0gqj9gN2ce2GZ0E7u6H1E9C3k1E0b4T8j5K2b0M2O', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@ribinamart.com');

INSERT INTO users (name, email, password_hash, role)
SELECT 'TechHub Electronics', 'seller1@ribinamart.com', '$2a$12$K8Kq8N94JqgO0gqj9gN2ce2GZ0E7u6H1E9C3k1E0b4T8j5K2b0M2O', 'SELLER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'seller1@ribinamart.com');

INSERT INTO users (name, email, password_hash, role)
SELECT 'PageTurner Bookstore', 'seller2@ribinamart.com', '$2a$12$K8Kq8N94JqgO0gqj9gN2ce2GZ0E7u6H1E9C3k1E0b4T8j5K2b0M2O', 'SELLER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'seller2@ribinamart.com');

INSERT INTO users (name, email, password_hash, role)
SELECT 'Aditya Buyer', 'buyer1@ribinamart.com', '$2a$12$K8Kq8N94JqgO0gqj9gN2ce2GZ0E7u6H1E9C3k1E0b4T8j5K2b0M2O', 'BUYER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'buyer1@ribinamart.com');

INSERT INTO users (name, email, password_hash, role)
SELECT 'Kavya Buyer', 'buyer2@ribinamart.com', '$2a$12$K8Kq8N94JqgO0gqj9gN2ce2GZ0E7u6H1E9C3k1E0b4T8j5K2b0M2O', 'BUYER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'buyer2@ribinamart.com');
