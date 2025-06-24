CREATE TABLE IF NOT EXISTS shopping_cart (
    shopping_cart_id UUID PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    state BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS shopping_cart_products (
    shopping_cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (shopping_cart_id, product_id),
    CONSTRAINT fk_cart FOREIGN KEY (shopping_cart_id)
    REFERENCES shopping_cart (shopping_cart_id)
);