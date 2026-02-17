-- V1__create_schema.sql
-- Criação completa do schema lógico da aplicação (tabelas + índices + FKs)

CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    active BOOLEAN NOT NULL DEFAULT true,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked BOOLEAN NOT NULL DEFAULT FALSE,
    locked_reason VARCHAR(255),
    locked_at TIMESTAMP,
    unlocked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE refresh_token (
    id UUID PRIMARY KEY,
    token VARCHAR(500) NOT NULL,
    user_id UUID NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE product (
    id UUID PRIMARY KEY,
    sku VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    short_description VARCHAR(500) NOT NULL,
    full_description TEXT,
    sale_price DECIMAL(19, 4) NOT NULL,
    cost_price DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'BRL',
    status VARCHAR(50) NOT NULL,
    supplier_sku VARCHAR(100),
    supplier_name VARCHAR(255),
    supplier_product_url VARCHAR(1000),
    lead_time_days INTEGER,
    is_dropship BOOLEAN NOT NULL DEFAULT true,
    weight DECIMAL(10, 4),
    length DECIMAL(10, 4),
    width DECIMAL(10, 4),
    height DECIMAL(10, 4),
    slug VARCHAR(255) NOT NULL,
    category_id UUID,
    brand VARCHAR(255),
    meta_title VARCHAR(255),
    meta_description VARCHAR(500),
    compare_at_price DECIMAL(19, 4),
    stock_quantity INTEGER,
    tags TEXT,
    attributes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_products_sku UNIQUE (sku),
    CONSTRAINT uk_products_slug UNIQUE (slug)
);

CREATE TABLE rotina (
    id UUID PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    icon VARCHAR(100),
    path VARCHAR(30),
    active BOOLEAN NOT NULL DEFAULT true,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_rotina_code UNIQUE (code)
);

CREATE TABLE perfil (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    icon VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true,
    display_order INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_perfil_code UNIQUE (code)
);

CREATE TABLE perfil_rotina (
    perfil_id UUID NOT NULL,
    rotina_id UUID NOT NULL,
    PRIMARY KEY (perfil_id, rotina_id),
    CONSTRAINT fk_perfil_rotina_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id) ON DELETE CASCADE,
    CONSTRAINT fk_perfil_rotina_rotina FOREIGN KEY (rotina_id) REFERENCES rotina (id) ON DELETE CASCADE
);

CREATE TABLE user_perfil (
    user_id UUID NOT NULL,
    perfil_id UUID NOT NULL,
    PRIMARY KEY (user_id, perfil_id),
    CONSTRAINT fk_user_perfil_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_perfil_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id) ON DELETE CASCADE
);

CREATE TABLE senhas_temporarias (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_temp_pass_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE bloqueio (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    login VARCHAR(255) NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_do_bloqueio TIMESTAMP NOT NULL,
    data_do_desbloqueio TIMESTAMP,
    data_do_usuario_desbloqueou TIMESTAMP,
    desbloqueado_por VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bloqueio_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE password_reset_requests (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    ip VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

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
    CONSTRAINT fk_product_file_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- Índices

CREATE INDEX idx_users_email ON users(email);

CREATE INDEX idx_refresh_token_token ON refresh_token(token);
CREATE INDEX idx_refresh_token_user_id ON refresh_token(user_id);

CREATE INDEX idx_product_sku ON product(sku);
CREATE INDEX idx_product_slug ON product(slug);
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_category_id ON product(category_id);

CREATE INDEX ix_rotina_code ON rotina (code);
CREATE INDEX ix_perfil_code ON perfil (code);

CREATE INDEX ix_perfil_rotina_rotina_id ON perfil_rotina (rotina_id);
CREATE INDEX ix_user_perfil_perfil_id ON user_perfil (perfil_id);

CREATE INDEX idx_temp_pass_user ON senhas_temporarias(user_id);
CREATE INDEX idx_temp_pass_expires ON senhas_temporarias(expires_at);

CREATE INDEX idx_bloqueio_user ON bloqueio(user_id);
CREATE INDEX idx_bloqueio_status ON bloqueio(status);

CREATE INDEX idx_prr_email ON password_reset_requests(email);
CREATE INDEX idx_prr_created ON password_reset_requests(created_at);
CREATE INDEX idx_prr_ip ON password_reset_requests(ip);

CREATE INDEX idx_users_locked ON users(locked);

