-- Altera a coluna 'desbloqueado_por' para armazenar nome (VARCHAR) em vez de UUID
ALTER TABLE dropshipping.bloqueio
    ALTER COLUMN desbloqueado_por TYPE VARCHAR(255) USING desbloqueado_por::text;

