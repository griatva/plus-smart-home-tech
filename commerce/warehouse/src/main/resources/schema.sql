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