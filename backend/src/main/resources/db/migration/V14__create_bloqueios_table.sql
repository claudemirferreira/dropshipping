-- Cria tabela de bloqueios de usuários
CREATE TABLE IF NOT EXISTS dropshipping.bloqueios (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    login VARCHAR(255) NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL, -- 'ativo' | 'inativo'
    data_do_bloqueio TIMESTAMP NOT NULL,
    data_do_desbloqueio TIMESTAMP,
    data_do_usuario_desbloqueou TIMESTAMP,
    desbloqueado_por UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bloqueios_user FOREIGN KEY (user_id) REFERENCES dropshipping.users (id)
);

CREATE INDEX IF NOT EXISTS idx_bloqueios_user ON dropshipping.bloqueios(user_id);
CREATE INDEX IF NOT EXISTS idx_bloqueios_status ON dropshipping.bloqueios(status);

