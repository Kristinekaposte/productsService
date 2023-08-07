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