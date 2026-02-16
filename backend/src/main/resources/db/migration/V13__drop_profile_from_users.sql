-- Remove a coluna profile da tabela users.
-- O acesso passa a ser definido apenas pelos perfis (tabela user_perfil).
ALTER TABLE users DROP COLUMN IF EXISTS profile;
