CREATE TABLE product_images (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    url VARCHAR(1000) NOT NULL,
    position INTEGER NOT NULL DEFAULT 0,
    is_main BOOLEAN NOT NULL DEFAULT false,
    alt_text VARCHAR(500),
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);
