-- Adiciona colunas de bloqueio e controle de tentativas de login na tabela users
ALTER TABLE dropshipping.users
    ADD COLUMN IF NOT EXISTS failed_login_attempts INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS locked BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS locked_reason VARCHAR(255),
    ADD COLUMN IF NOT EXISTS locked_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS unlocked_at TIMESTAMP;

-- Índice opcional para consultas por status de bloqueio
CREATE INDEX IF NOT EXISTS idx_users_locked ON dropshipping.users(locked);

