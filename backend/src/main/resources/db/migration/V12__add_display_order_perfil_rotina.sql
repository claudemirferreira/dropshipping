-- display_order para ordenação no menu
ALTER TABLE perfil ADD COLUMN display_order INT NOT NULL DEFAULT 0;
ALTER TABLE rotina ADD COLUMN display_order INT NOT NULL DEFAULT 0;

-- Perfis: ADMIN=1, MANAGER=2, SELLER=3, OPERATOR=4
UPDATE perfil SET display_order = 1 WHERE code = 'ADMIN';
UPDATE perfil SET display_order = 2 WHERE code = 'MANAGER';
UPDATE perfil SET display_order = 3 WHERE code = 'SELLER';
UPDATE perfil SET display_order = 4 WHERE code = 'OPERATOR';

-- Rotinas com path: ordem do menu (dashboard, usuarios, produtos, rotinas, perfis)
UPDATE rotina SET display_order = 1 WHERE code = 'dashboard:ver';
UPDATE rotina SET display_order = 2 WHERE code = 'usuarios:listar';
UPDATE rotina SET display_order = 3 WHERE code = 'produtos:listar';
UPDATE rotina SET display_order = 4 WHERE code = 'rotinas:listar';
UPDATE rotina SET display_order = 5 WHERE code = 'perfis:listar';
-- demais rotinas sem path ou sem ordem específica permanecem 0
