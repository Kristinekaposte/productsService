use marketdb;


DROP TABLE IF EXISTS  order_item;
DROP TABLE IF EXISTS  orders;
DROP TABLE IF EXISTS  product;
DROP TABLE IF EXISTS  category;
DROP TABLE IF EXISTS  customer;
DROP TABLE IF EXISTS  address;

CREATE TABLE address (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  phone_number VARCHAR(11) NOT NULL,
  country VARCHAR(30) NOT NULL,
  city VARCHAR(30) NOT NULL,
  postal_code VARCHAR(10) NOT NULL
);
CREATE TABLE customer (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  email VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(64) NOT NULL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  address_id BIGINT NOT NULL,
  FOREIGN KEY (address_id) REFERENCES address(id)
);
CREATE TABLE category (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(125) UNIQUE NOT NULL
);

CREATE TABLE products (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(125) NOT NULL,
  description VARCHAR (255),
  price DOUBLE NOT NULL,
  quantity INTEGER NOT NULL,
  category_id BIGINT NOT NULL,
  FOREIGN KEY (category_id) REFERENCES category(id)
);
CREATE TABLE orders (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  order_number VARCHAR(20) NOT NULL,
  customer_id BIGINT,
  order_date DATETIME NOT NULL,
  total_price DOUBLE NOT NULL,
  FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE order_item (
  id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  order_id BIGINT,
  product_id BIGINT,
  item_price DOUBLE NOT NULL,
  quantity INTEGER NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (product_id) REFERENCES products(id)
);


