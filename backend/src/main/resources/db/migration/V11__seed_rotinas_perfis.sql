-- Rotinas (UUIDs fixos para referência)
INSERT INTO rotina (id, code, name, description, icon, path, active, created_at, updated_at) VALUES
  ('a1000000-0000-0000-0000-000000000001', 'usuarios', 'Usuários', NULL, 'pi pi-users', '/usuarios', true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000002', 'rotinas', 'Rotinas', NULL, 'pi pi-list', '/rotinas', true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000003', 'perfis', 'Perfis', NULL, 'pi pi-id-card', '/perfis', true, NOW(), NOW()),
  ('a1000000-0000-0000-0000-000000000004', 'produtos', 'Produtos', NULL, 'pi pi-id-card', '/produtos', true, NOW(), NOW());

-- Perfis (UUIDs fixos)
INSERT INTO perfil (id, code, name, description, icon, active, created_at, updated_at) VALUES
  ('b2000000-0000-0000-0000-000000000001', 'ADMIN', 'Administrador', 'Acesso total ao sistema', 'pi pi-shield', true, NOW(), NOW()),
  ('b2000000-0000-0000-0000-000000000002', 'MANAGER', 'Gerente', 'Gestão de vendas e usuários', 'pi pi-briefcase', true, NOW(), NOW()),
  ('b2000000-0000-0000-0000-000000000003', 'SELLER', 'Vendedor', 'Acesso às vendas', 'pi pi-shopping-cart', true, NOW(), NOW()),
  ('b2000000-0000-0000-0000-000000000004', 'OPERATOR', 'Operador', 'Operações internas', 'pi pi-cog', true, NOW(), NOW());


-- ADMIN: produtos + usuarios + dashboard
INSERT INTO perfil_rotina (perfil_id, rotina_id) VALUES
  ('b2000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000001'),
  ('b2000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000002'),
  ('b2000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000003');

-- SELLER: dashboard + produtos listar
INSERT INTO perfil_rotina (perfil_id, rotina_id) VALUES
  ('b2000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000004');

INSERT INTO dropshipping.users
(id, email, password_hash, "name", phone, active, profile, created_at, updated_at)
VALUES('f55fee9b-0289-4a6d-89e3-0d63971d29e5'::uuid, 'admin@dropshipping.com', '$2a$10$YZgvkNd01XN5ezFLybUuTuHGemBbPzBehdxfNmTbaYKsyx6XeeaoO', 'Admin Dropshipping', NULL, true, 'ADMIN', '2026-02-07 19:37:02.353', '2026-02-07 19:37:02.353');
INSERT INTO dropshipping.users
(id, email, password_hash, "name", phone, active, profile, created_at, updated_at)
VALUES('0d7c383b-1282-4a5d-99be-1b9c1c5af983'::uuid, 'claudemirramosferreira@gmail.com', '$2a$10$i7JHe7cjc7weH7agisAdju9ScIhykpf81vpbnkARaW8HPZ9q4ntEa', 'João Silva', 'string', true, 'ADMIN', '2026-02-07 19:39:12.130', '2026-02-07 19:39:12.130');
INSERT INTO dropshipping.users
(id, email, password_hash, "name", phone, active, profile, created_at, updated_at)
VALUES('fa712e41-b6d1-413a-a6b7-77037ab8719e'::uuid, 'bratty@gmail.com', '$2a$10$3Nz7uM.rGVaSHFENQT3RVeZUNqm5TYAbWW/vGUwcZIpm/5PSQqRMK', 'Bretty Jane', '92992573036', true, 'ADMIN', '2026-02-07 20:48:37.601', '2026-02-07 20:48:37.601');
INSERT INTO dropshipping.users
(id, email, password_hash, "name", phone, active, profile, created_at, updated_at)
VALUES('03423fea-3818-4c8c-b9fe-c47deaa9115b'::uuid, 'ze@gmail.com', '$2a$10$/EcCKvy3BtleW17ezB3hKO2pKRI7LcyzfJ98i612bfRtW8bxbuDiW', 'Ze Luiz', '06011977', true, 'ADMIN', '2026-02-07 22:20:25.476', '2026-02-07 22:20:25.476');

-- Migrar users.profile -> user_perfil (mapear profile enum para perfil_id)
INSERT INTO user_perfil (user_id, perfil_id) VALUES
('f55fee9b-0289-4a6d-89e3-0d63971d29e5', 'b2000000-0000-0000-0000-000000000001'),
('03423fea-3818-4c8c-b9fe-c47deaa9115b', 'b2000000-0000-0000-0000-000000000003');
