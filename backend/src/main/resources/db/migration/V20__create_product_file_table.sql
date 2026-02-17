CREATE TABLE product_file (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    object_name VARCHAR(1000) NOT NULL,
    original_name VARCHAR(500),
    position INTEGER NOT NULL DEFAULT 0,
    is_main BOOLEAN NOT NULL DEFAULT false,
    alt_text VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_product_file_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

