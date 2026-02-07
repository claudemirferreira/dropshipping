-- Senha para todos: Senha@123 (hash bcrypt)
-- Hash gerado para: Senha@123
INSERT INTO users (id, email, password_hash, name, phone, active, profile, created_at, updated_at)
VALUES
  ('11111111-1111-1111-1111-111111111101', 'admin@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin Sistema', NULL, true, 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('11111111-1111-1111-1111-111111111102', 'manager@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Manager Vendas', NULL, true, 'MANAGER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('11111111-1111-1111-1111-111111111103', 'seller@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Vendedor Exemplo', NULL, true, 'SELLER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('11111111-1111-1111-1111-111111111104', 'operator@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Operador Sistema', NULL, true, 'OPERATOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
;
