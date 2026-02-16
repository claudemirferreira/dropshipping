-- Cria tabela para senhas temporárias de recuperação
CREATE TABLE IF NOT EXISTS dropshipping.senhas_temporarias (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_temp_pass_user FOREIGN KEY (user_id) REFERENCES dropshipping.users (id)
);

CREATE INDEX IF NOT EXISTS idx_temp_pass_user ON dropshipping.senhas_temporarias(user_id);
CREATE INDEX IF NOT EXISTS idx_temp_pass_expires ON dropshipping.senhas_temporarias(expires_at);

