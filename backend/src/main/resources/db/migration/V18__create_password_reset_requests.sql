-- Auditoria de solicitações de recuperação de senha
CREATE TABLE IF NOT EXISTS dropshipping.password_reset_requests (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    ip VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_prr_email ON dropshipping.password_reset_requests(email);
CREATE INDEX IF NOT EXISTS idx_prr_created ON dropshipping.password_reset_requests(created_at);
CREATE INDEX IF NOT EXISTS idx_prr_ip ON dropshipping.password_reset_requests(ip);
