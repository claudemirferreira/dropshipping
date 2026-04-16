CREATE TABLE IF NOT EXISTS seller (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    access_token TEXT NOT NULL,
    token_type VARCHAR(32) NOT NULL,
    expires_in INT NOT NULL,
    scope TEXT NOT NULL,
    marketplace_id BIGINT NOT NULL,
    marketplace VARCHAR(60) NOT NULL,
    refresh_token TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_seller_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uk_seller_marketplace_pair UNIQUE (marketplace, user_id)
);
