CREATE TABLE IF NOT EXISTS payment (
    payment_id UUID PRIMARY KEY NOT NULL,
    order_id UUID NOT NULL,
    total_payment NUMERIC NOT NULL,
    delivery_total NUMERIC NOT NULL,
    fee_total NUMERIC NOT NULL,
    product_total NUMERIC NOT NULL,
    payment_state VARCHAR(50) NOT NULL
);