-- V3__reseed_admin_perfil.sql
-- Garante que o usuário admin@dropshipping.com tenha o perfil ADMIN e que os perfis padrão existam.

-- Perfis padrão (caso tenham sido apagados)
INSERT INTO dropshipping.perfil (id, code, name, icon, active, created_at, updated_at)
VALUES
  ('b2000000-0000-0000-0000-000000000001', 'ADMIN',   'Administrador', 'pi pi-shield',        true, NOW(), NOW()),
  ('b2000000-0000-0000-0000-000000000002', 'MANAGER', 'Gerente',       'pi pi-briefcase',     true, NOW(), NOW()),
  ('b2000000-0000-0000-0000-000000000003', 'SELLER',  'Vendedor',      'pi pi-shopping-cart', true, NOW(), NOW()),
  ('b2000000-0000-0000-0000-000000000004', 'OPERATOR','Operador',      'pi pi-cog',           true, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Vincula o usuário admin@dropshipping.com ao perfil ADMIN
INSERT INTO dropshipping.user_perfil (user_id, perfil_id)
SELECT u.id, p.id
FROM dropshipping.users u
JOIN dropshipping.perfil p ON p.code = 'ADMIN'
WHERE u.email = 'admin@dropshipping.com'
ON CONFLICT DO NOTHING;

