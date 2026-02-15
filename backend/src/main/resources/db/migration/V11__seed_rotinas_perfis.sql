-- Rotinas (UUIDs fixos para referência)
INSERT INTO rotina (id, code, name, description, icon, path, active, created_at, updated_at) VALUES
  ('a1000000-0000-0000-0000-000000000001', 'produtos:listar', 'Listar produtos', NULL, 'pi pi-box', '/produtos', true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000002', 'produtos:criar', 'Criar produto', NULL, 'pi pi-plus', NULL, true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000003', 'produtos:editar', 'Editar produto', NULL, 'pi pi-pencil', NULL, true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000004', 'produtos:excluir', 'Excluir produto', NULL, 'pi pi-trash', NULL, true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000005', 'usuarios:listar', 'Listar usuários', NULL, 'pi pi-users', '/usuarios', true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000006', 'usuarios:criar', 'Criar usuário', NULL, 'pi pi-user-plus', NULL, true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000007', 'usuarios:editar', 'Editar usuário', NULL, 'pi pi-pencil', NULL, true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000008', 'usuarios:excluir', 'Excluir usuário', NULL, 'pi pi-trash', NULL, true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000009', 'dashboard:ver', 'Ver dashboard', NULL, 'pi pi-home', '/dashboard', true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000010', 'rotinas:listar', 'Listar rotinas', NULL, 'pi pi-list', '/rotinas', true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000011', 'perfis:listar', 'Listar perfis', NULL, 'pi pi-id-card', '/perfis', true, NOW(), NOW());

-- Perfis (UUIDs fixos)
INSERT INTO perfil (id, code, name, description, icon, active, created_at, updated_at) VALUES
  ('b2000000-0000-0000-0000-000000000001', 'ADMIN', 'Administrador', 'Acesso total ao sistema', 'pi pi-shield', true, NOW(), NOW()),
  ('b2000000-0000-0000-0000-000000000002', 'MANAGER', 'Gerente', 'Gestão de vendas e usuários', 'pi pi-briefcase', true, NOW(), NOW()),
  ('b2000000-0000-0000-0000-000000000003', 'SELLER', 'Vendedor', 'Acesso às vendas', 'pi pi-shopping-cart', true, NOW(), NOW()),
  ('b2000000-0000-0000-0000-000000000004', 'OPERATOR', 'Operador', 'Operações internas', 'pi pi-cog', true, NOW(), NOW());

-- perfil_rotina: ADMIN tem todas as rotinas
INSERT INTO perfil_rotina (perfil_id, rotina_id)
SELECT 'b2000000-0000-0000-0000-000000000001', id FROM rotina;

-- MANAGER: produtos + usuarios + dashboard
INSERT INTO perfil_rotina (perfil_id, rotina_id) VALUES
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000001'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000002'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000003'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000004'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000005'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000006'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000007'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000008'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000009'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000010'),
  ('b2000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000011');

-- SELLER: dashboard + produtos listar
INSERT INTO perfil_rotina (perfil_id, rotina_id) VALUES
  ('b2000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001'),
  ('b2000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000009');

-- OPERATOR: dashboard + produtos listar + usuarios listar
INSERT INTO perfil_rotina (perfil_id, rotina_id) VALUES
  ('b2000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000001'),
  ('b2000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000005'),
  ('b2000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000009');

-- Migrar users.profile -> user_perfil (mapear profile enum para perfil_id)
INSERT INTO user_perfil (user_id, perfil_id)
SELECT u.id, 'b2000000-0000-0000-0000-000000000001' FROM users u WHERE u.profile = 'ADMIN';

INSERT INTO user_perfil (user_id, perfil_id)
SELECT u.id, 'b2000000-0000-0000-0000-000000000002' FROM users u WHERE u.profile = 'MANAGER';

INSERT INTO user_perfil (user_id, perfil_id)
SELECT u.id, 'b2000000-0000-0000-0000-000000000003' FROM users u WHERE u.profile = 'SELLER';

INSERT INTO user_perfil (user_id, perfil_id)
SELECT u.id, 'b2000000-0000-0000-0000-000000000004' FROM users u WHERE u.profile = 'OPERATOR';
