-- V6__perfil_system_default_and_audit.sql
-- Adiciona suporte a perfis protegidos pelo sistema e tabela de auditoria de edição de perfis.

-- 1. Adiciona coluna system_default na tabela perfil
--    Perfis com system_default = true não podem ser editados nem excluídos via interface.
ALTER TABLE perfil
    ADD COLUMN system_default BOOLEAN NOT NULL DEFAULT false;

-- 2. Marca perfis padrão do sistema como protegidos
UPDATE perfil SET system_default = true WHERE code IN ('ADMIN', 'MANAGER');

-- 3. Cria tabela de auditoria para edições de perfil
--    Registra: quem editou, quando, e o que mudou (snapshot antes/depois em JSON).
CREATE TABLE perfil_audit_log (
    id             UUID PRIMARY KEY,
    perfil_id      UUID         NOT NULL,
    edited_by      VARCHAR(255) NOT NULL,
    edited_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    field_name     VARCHAR(100) NOT NULL,
    value_before   TEXT,
    value_after    TEXT,
    CONSTRAINT fk_perfil_audit_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id) ON DELETE CASCADE
);

CREATE INDEX idx_perfil_audit_perfil_id ON perfil_audit_log (perfil_id);
CREATE INDEX idx_perfil_audit_edited_at ON perfil_audit_log (edited_at);
