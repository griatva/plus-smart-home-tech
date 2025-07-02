CREATE TABLE IF NOT EXISTS warehouse_item (
    warehouse_item_id UUID PRIMARY KEY,
    product_id UUID UNIQUE NOT NULL,
    width NUMERIC NOT NULL,
    height NUMERIC NOT NULL,
    depth NUMERIC NOT NULL,
    weight NUMERIC NOT NULL,
    fragile BOOLEAN,
    quantity BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_booking (
    booking_id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    delivery_id UUID
);

CREATE TABLE IF NOT EXISTS order_booking_products (
    booking_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (booking_id, product_id),
    CONSTRAINT fk_order_booking FOREIGN KEY (booking_id) REFERENCES order_booking(booking_id) ON DELETE CASCADE
);