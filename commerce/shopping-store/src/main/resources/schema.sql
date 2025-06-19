CREATE TABLE IF NOT EXISTS products (
    product_id UUID PRIMARY KEY NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_src TEXT,
    quantity_state VARCHAR(50) NOT NULL,
    product_state VARCHAR(50) NOT NULL,
    product_category VARCHAR(50),
    price NUMERIC(19, 2) NOT NULL
);