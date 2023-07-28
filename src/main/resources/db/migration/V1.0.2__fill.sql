INSERT INTO address (phone_number, country, city, postal_code)
VALUES
  ('1234567890', 'USA', 'New York', '10001'),
  ('9876543210', 'UK', 'London', '20001'),
  ('5554443333', 'Canada', 'Toronto', '30001');

INSERT INTO customer (email, password, first_name, last_name, address_id)
VALUES
  ('john.doe@example.com', 'password123', 'John', 'Doe', 1),
  ('jane.smith@example.com', 'pass456word', 'Jane', 'Smith', 2),
  ('bob.johnson@example.com', 'secret123', 'Bob', 'Johnson', 3);

INSERT INTO category (name)
VALUES
  ('Electronics'),
  ('Clothing'),
  ('Books');

INSERT INTO products (name, description, price, quantity, category_id)
VALUES
  ('Smartphone', 'Smartphone', 799.99, 50, 1),
  ('T-shirt', 'Blue T-shirt', 19.99, 100, 2),
  ('Novel', 'Mystery novel', 12.49, 75, 3);

INSERT INTO orders (order_number, customer_id, order_date, total_price)
VALUES
  ('ORD-123456', 1, '2023-07-20 10:30:00', 1239.92),
  ('ORD-987654', 2, '2023-07-20 11:45:00', 59.97),
  ('ORD-555444', 3, '2023-07-20 13:15:00', 49.96);
INSERT INTO order_item (order_id, product_id, item_price, quantity)
VALUES
  (1, 1, 799.99, 2),
  (1, 2, 19.99, 3),
  (2, 2, 19.99, 1),
  (3, 3, 12.49, 4);

