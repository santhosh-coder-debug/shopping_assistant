CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE products (
 id BIGSERIAL PRIMARY KEY,
 sku VARCHAR(255) NOT NULL UNIQUE,
 name VARCHAR(255) NOT NULL,
 brand VARCHAR(255),
 category VARCHAR(255),
 description TEXT,
 price NUMERIC(12,2) NOT NULL CHECK(price >= 0),
 currency VARCHAR(3) NOT NULL,
 stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK(stock_quantity >= 0),
 active BOOLEAN NOT NULL DEFAULT TRUE
);
CREATE INDEX idx_products_catalog ON products(category,brand,price) WHERE active;
INSERT INTO products(sku,name,brand,category,description,price,currency,stock_quantity) VALUES
 ('LAP-001','Creator Laptop 14','Northstar','Laptop','14-inch lightweight laptop with 16GB RAM, 512GB SSD, long battery life and strong everyday productivity performance.',899.00,'USD',12),
 ('LAP-002','Studio Laptop 16','Northstar','Laptop','16-inch laptop with 32GB RAM, 1TB SSD and discrete graphics for video editing and content creation.',1499.00,'USD',5),
 ('PHN-001','Camera Phone Pro','Orbit','Phone','Smartphone with optical image stabilization, telephoto camera and all-day battery.',749.00,'USD',20),
 ('PHN-002','Everyday Phone','Orbit','Phone','Affordable smartphone with 128GB storage, fast charging and dual cameras.',399.00,'USD',15),
 ('AUD-001','Wireless Headphones','SoundPeak','Audio','Noise cancelling wireless headphones with 30-hour battery life.',179.00,'USD',35);
