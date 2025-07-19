-- Insert sample users
INSERT INTO users (email, name, created_at) VALUES
('john.doe@example.com', 'John Doe', '2024-01-15 10:30:00'),
('jane.smith@example.com', 'Jane Smith', '2024-01-16 14:20:00'),
('bob.johnson@example.com', 'Bob Johnson', '2024-01-17 09:45:00'),
('alice.williams@example.com', 'Alice Williams', '2024-01-18 16:15:00'),
('charlie.brown@example.com', 'Charlie Brown', '2024-01-19 11:00:00');

-- Insert sample user profiles (one-to-one relationship)
INSERT INTO user_profiles (user_id, phone_number, birth_date, bio, profile_picture_url) VALUES
(1, '+1-555-0101', '1990-05-15', 'Software developer with 5+ years experience', 'https://example.com/avatars/john.jpg'),
(2, '+1-555-0102', '1985-09-22', 'Product manager and tech enthusiast', 'https://example.com/avatars/jane.jpg'),
(3, '+1-555-0103', '1992-03-10', 'Designer and creative thinker', NULL),
(4, '+1-555-0104', '1988-11-30', 'Data analyst and problem solver', 'https://example.com/avatars/alice.jpg');

-- Insert sample products
INSERT INTO products (name, description, price, stock_quantity, created_at) VALUES
('Laptop Pro 15"', 'High-performance laptop with 16GB RAM and 512GB SSD', 1299.99, 50, '2024-01-10 08:00:00'),
('Wireless Mouse', 'Ergonomic wireless mouse with precision tracking', 49.99, 200, '2024-01-10 08:15:00'),
('Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches', 129.99, 75, '2024-01-10 08:30:00'),
('Monitor 27"', '4K UHD monitor with HDR support', 399.99, 30, '2024-01-10 08:45:00'),
('Webcam HD', 'Full HD webcam with auto-focus and noise cancellation', 89.99, 100, '2024-01-10 09:00:00'),
('Headphones', 'Noise-cancelling over-ear headphones', 199.99, 60, '2024-01-10 09:15:00'),
('USB-C Hub', 'Multi-port USB-C hub with HDMI, USB 3.0, and charging', 79.99, 150, '2024-01-10 09:30:00'),
('Smartphone', 'Latest flagship smartphone with 128GB storage', 899.99, 25, '2024-01-10 09:45:00');

-- Insert sample orders (many-to-one relationship with users)
INSERT INTO orders (order_number, total_amount, status, created_at, user_id) VALUES
('ORD-2024-001', 1549.98, 'DELIVERED', '2024-01-20 10:00:00', 1),
('ORD-2024-002', 579.97, 'SHIPPED', '2024-01-21 14:30:00', 2),
('ORD-2024-003', 1299.99, 'PROCESSING', '2024-01-22 09:15:00', 3),
('ORD-2024-004', 289.98, 'DELIVERED', '2024-01-23 16:45:00', 4),
('ORD-2024-005', 899.99, 'PENDING', '2024-01-24 11:20:00', 1),
('ORD-2024-006', 129.99, 'CANCELLED', '2024-01-25 13:10:00', 2),
('ORD-2024-007', 469.98, 'SHIPPED', '2024-01-26 15:30:00', 5),
('ORD-2024-008', 179.98, 'DELIVERED', '2024-01-27 08:50:00', 3);

-- Insert sample order_products (many-to-many relationship)
INSERT INTO order_products (order_id, product_id) VALUES
-- Order 1: Laptop + Wireless Mouse
(1, 1), (1, 2),
-- Order 2: Monitor + Keyboard + Mouse
(2, 4), (2, 3), (2, 2),
-- Order 3: Laptop only
(3, 1),
-- Order 4: Webcam + Headphones
(4, 5), (4, 6),
-- Order 5: Smartphone only
(5, 8),
-- Order 6: Keyboard only (cancelled)
(6, 3),
-- Order 7: Monitor + USB-C Hub
(7, 4), (7, 7),
-- Order 8: Wireless Mouse + Webcam
(8, 2), (8, 5);