# Prompt para Implementação: Controle de Acesso – Rotinas e Perfis

## Contexto do Projeto

- **Stack:** Spring Boot 4.0.2, Java 25, Spring Data JPA, PostgreSQL, Lombok
- **Banco de dados:** PostgreSQL (schema `dropshipping`)
- **Versionamento do banco:** Flyway
- **Pacote base:** `com.srv.setebit.dropshipping`
- **Arquitetura:** Clean Architecture + DDD

## Objetivo desta Fase

Implementar o controle de acesso baseado em **rotinas** e **perfis**, permitindo:

1. **Rotinas:** cadastrar as funcionalidades/rotinas do sistema (ex: `produtos:listar`, `usuarios:criar`)
2. **Perfis:** agrupar rotinas em perfis nomeados (ex: "Administrador", "Gerente de Vendas")
3. **Perfil ↔ Rotina:** atribuir rotinas a um perfil (N:N)
4. **Usuário ↔ Perfil:** atribuir perfis a um usuário (N:N)

Assim, um usuário terá acesso a uma rotina se **pelo menos um** de seus perfis contiver essa rotina.

---

## 1. Modelo de Dados – Tabelas

### 1.1 Tabela `rotina`

Representa uma funcionalidade/rotina do sistema.

| Coluna        | Tipo        | Obrigatório | Descrição |
|---------------|-------------|-------------|-----------|
| `id`          | UUID        | Sim         | Chave primária |
| `code`        | VARCHAR(100)| Sim         | Código único da rotina (ex: `produtos:listar`, `usuarios:criar`) |
| `name`        | VARCHAR(255)| Sim         | Nome legível |
| `description` | VARCHAR(500)| Não         | Descrição da rotina |
| `icon`        | VARCHAR(100)| Não         | Ícone para exibição no front (ex: classe CSS, nome do ícone) |
| `path`        | VARCHAR(30) | Não         | Rota da página no frontend |
| `active`      | BOOLEAN     | Sim         | Se a rotina está ativa (default true) |
| `created_at`  | TIMESTAMP   | Sim         | Data de criação |
| `updated_at`  | TIMESTAMP   | Sim         | Data de atualização |

- **Constraint:** `code` único (UNIQUE)
- **Índice:** `code` para buscas rápidas

### 1.2 Tabela `perfil`

Representa um perfil de acesso (ex: Administrador, Gerente, Operador).

| Coluna        | Tipo        | Obrigatório | Descrição |
|---------------|-------------|-------------|-----------|
| `id`          | UUID        | Sim         | Chave primária |
| `code`        | VARCHAR(50) | Sim         | Código único (ex: `ADMIN`, `MANAGER`, `SELLER`) |
| `name`        | VARCHAR(255)| Sim         | Nome legível |
| `description` | VARCHAR(500)| Não         | Descrição do perfil |
| `icon`        | VARCHAR(100)| Não         | Ícone para exibição no front (ex: classe CSS, nome do ícone) |
| `active`      | BOOLEAN     | Sim         | Se o perfil está ativo (default true) |
| `created_at`  | TIMESTAMP   | Sim         | Data de criação |
| `updated_at`  | TIMESTAMP   | Sim         | Data de atualização |

- **Constraint:** `code` único (UNIQUE)
- **Índice:** `code`

### 1.3 Tabela `perfil_rotina`

Tabela de associação N:N entre perfil e rotina.

| Coluna     | Tipo | Obrigatório | Descrição |
|------------|------|-------------|-----------|
| `perfil_id` | UUID | Sim | FK para `perfil.id` |
| `rotina_id` | UUID | Sim | FK para `rotina.id` |

- **PK composta:** `(perfil_id, rotina_id)`
- **FK:** `perfil_id` → `perfil(id)` ON DELETE CASCADE
- **FK:** `rotina_id` → `rotina(id)` ON DELETE CASCADE
- **Índice:** `rotina_id` (para consulta reversa: quais perfis têm esta rotina?)

### 1.4 Tabela `user_perfil`

Tabela de associação N:N entre usuário e perfil.

| Coluna     | Tipo | Obrigatório | Descrição |
|------------|------|-------------|-----------|
| `user_id`  | UUID | Sim | FK para `users.id` |
| `perfil_id`| UUID | Sim | FK para `perfil.id` |

- **PK composta:** `(user_id, perfil_id)`
- **FK:** `user_id` → `users(id)` ON DELETE CASCADE
- **FK:** `perfil_id` → `perfil(id)` ON DELETE CASCADE
- **Índice:** `perfil_id` (para consulta: quais usuários têm este perfil?)

---

## 2. Migrations Flyway

### V7__create_rotina_table.sql

```sql
CREATE TABLE rotina (
    id UUID PRIMARY KEY,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    icon VARCHAR(100),
    path VARCHAR(30),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_rotina_code UNIQUE (code)
);

CREATE INDEX ix_rotina_code ON rotina (code);
```

### V8__create_perfil_table.sql

```sql
CREATE TABLE perfil (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    icon VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_perfil_code UNIQUE (code)
);

CREATE INDEX ix_perfil_code ON perfil (code);
```

### V9__create_perfil_rotina_table.sql

```sql
CREATE TABLE perfil_rotina (
    perfil_id UUID NOT NULL,
    rotina_id UUID NOT NULL,
    PRIMARY KEY (perfil_id, rotina_id),
    CONSTRAINT fk_perfil_rotina_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id) ON DELETE CASCADE,
    CONSTRAINT fk_perfil_rotina_rotina FOREIGN KEY (rotina_id) REFERENCES rotina (id) ON DELETE CASCADE
);

CREATE INDEX ix_perfil_rotina_rotina_id ON perfil_rotina (rotina_id);
```

### V10__create_user_perfil_table.sql

```sql
CREATE TABLE user_perfil (
    user_id UUID NOT NULL,
    perfil_id UUID NOT NULL,
    PRIMARY KEY (user_id, perfil_id),
    CONSTRAINT fk_user_perfil_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_perfil_perfil FOREIGN KEY (perfil_id) REFERENCES perfil (id) ON DELETE CASCADE
);

CREATE INDEX ix_user_perfil_perfil_id ON user_perfil (perfil_id);
```

---

## 3. Dados Iniciais (Seed) – Opcional

### V11__seed_rotinas_perfis.sql

Cadastrar rotinas e perfis padrão, além da migração dos usuários existentes (coluna `profile` → `user_perfil`).

**Estratégia de migração:**

- A tabela `users` atualmente possui a coluna `profile` (enum: ADMIN, MANAGER, SELLER, OPERATOR).
- Opção A: Manter a coluna `profile` temporariamente e popular `user_perfil` com base nela; deprecar `profile` em fase posterior.
- Opção B: Criar perfis com codes ADMIN, MANAGER, SELLER, OPERATOR; popular `perfil_rotina` conforme mapeamento desejado; migrar `users.profile` → `user_perfil`; depois remover a coluna `profile` (nova migration).

**Exemplo de rotinas sugeridas:**

| code | name | path | icon |
|------|------|------|------|
| produtos:listar | Listar produtos | /produtos | pi pi-box |
| produtos:criar | Criar produto | - | pi pi-plus |
| produtos:editar | Editar produto | - | pi pi-pencil |
| produtos:excluir | Excluir produto | - | pi pi-trash |
| usuarios:listar | Listar usuários | /usuarios | pi pi-users |
| usuarios:criar | Criar usuário | - | pi pi-user-plus |
| usuarios:editar | Editar usuário | - | pi pi-pencil |
| usuarios:excluir | Excluir usuário | - | pi pi-trash |
| dashboard:ver | Ver dashboard | /dashboard | pi pi-home |

**Exemplo de perfis sugeridos:**

| code | name | icon |
|------|------|------|
| ADMIN | Administrador | pi pi-shield |
| MANAGER | Gerente | pi pi-briefcase |
| SELLER | Vendedor | pi pi-shopping-cart |
| OPERATOR | Operador | pi pi-cog |

---

## 4. Domínio (Entities)

### 4.1 Entidade `Rotina`

- `id`, `code`, `name`, `description`, `icon`, `path`, `active`, `createdAt`, `updatedAt`

### 4.2 Entidade `Perfil`

- `id`, `code`, `name`, `description`, `icon`, `active`, `createdAt`, `updatedAt`
- Relação com `Rotina`: muitos-para-muitos via `perfil_rotina`

### 4.3 Entidade `User`

- Atualizar para ter relação N:N com `Perfil` via `user_perfil`
- Decisão: manter ou remover o campo `profile` (enum) conforme estratégia de migração

---

## 5. Integração com Spring Security

### 5.1 Autorização por rotina

Em vez de `@PreAuthorize("hasRole('ADMIN')")`, usar verificação por rotina:

- Exemplo: `@PreAuthorize("hasAuthority('produtos:criar')")` ou método customizado que verifica se o usuário possui a rotina via seus perfis.

### 5.2 Fluxo de verificação

1. Usuário autenticado carrega seus perfis.
2. Para cada perfil, carregar as rotinas associadas.
3. Gerar a lista de rotinas (códigos) do usuário e expor como authorities (ex: via `UserDetails` ou `Authentication`).
4. No `JwtAuthenticationFilter` ou equivalente, popular as authorities a partir de `user_perfil` e `perfil_rotina`.

---

## 6. Use Cases sugeridos

### Rotinas
- `ListRotinasUseCase` – listar rotinas (paginado, com filtros)
- `CreateRotinaUseCase` – criar rotina
- `UpdateRotinaUseCase` – atualizar rotina
- `GetRotinaByIdUseCase` – buscar por ID

### Perfis
- `ListPerfisUseCase` – listar perfis (com rotinas associadas)
- `CreatePerfilUseCase` – criar perfil (com lista de rotina_ids)
- `UpdatePerfilUseCase` – atualizar perfil (e rotinas)
- `GetPerfilByIdUseCase` – buscar por ID
- `AssignRotinasToPerfilUseCase` – atribuir/remover rotinas de um perfil

### Usuário ↔ Perfil
- `AssignPerfisToUserUseCase` – atribuir perfis a um usuário
- `GetUserPerfisUseCase` – listar perfis do usuário
- `GetUserRotinasUseCase` – listar rotinas acessíveis pelo usuário (via perfis)

---

## 7. Endpoints REST sugeridos

Base path: `/api/v1`

### Rotinas (protegido: ex. rotinas:listar)
- `GET /rotinas` – listar com paginação e filtros
- `POST /rotinas` – criar (body: code, name, description, icon, path)
- `GET /rotinas/{id}` – buscar por ID
- `PUT /rotinas/{id}` – atualizar (body: code, name, description, icon, path, active)
- `DELETE /rotinas/{id}` – excluir (cuidado com FK em perfil_rotina)

### Perfis (protegido)
- `GET /perfis` – listar
- `POST /perfis` – criar (body: code, name, description, icon, rotinaIds[])
- `GET /perfis/{id}` – buscar por ID (com rotinas)
- `PUT /perfis/{id}` – atualizar (body: code, name, description, icon, active)
- `PUT /perfis/{id}/rotinas` – atualizar rotinas do perfil (replace)
- `DELETE /perfis/{id}` – excluir

### Usuários – perfis (protegido)
- `GET /users/{id}/perfis` – listar perfis do usuário
- `PUT /users/{id}/perfis` – atribuir perfis (body: perfilIds[])

---

## 8. Estrutura de pacotes sugerida

```
com.srv.setebit.dropshipping
├── domain/
│   ├── user/
│   │   ├── User.java
│   │   └── ...
│   ├── access/
│   │   ├── Rotina.java
│   │   ├── Perfil.java
│   │   ├── port/
│   │   │   ├── RotinaRepositoryPort.java
│   │   │   ├── PerfilRepositoryPort.java
│   │   │   └── UserPerfilRepositoryPort.java
│   │   └── exception/
│   │       ├── RotinaNotFoundException.java
│   │       └── PerfilNotFoundException.java
│   └── ...
├── application/
│   └── access/
│       ├── ListRotinasUseCase.java
│       ├── CreateRotinaUseCase.java
│       ├── ListPerfisUseCase.java
│       ├── CreatePerfilUseCase.java
│       ├── AssignPerfisToUserUseCase.java
│       └── dto/
│           ├── request/
│           └── response/
└── infrastructure/
    └── persistence/
        ├── jpa/
        │   ├── RotinaEntity.java
        │   ├── PerfilEntity.java
        │   ├── PerfilRotinaEntity.java (ou embeddable)
        │   ├── UserPerfilEntity.java
        │   └── ...
        └── adapter/
            └── ...
```

---

## 9. Considerações

1. **Compatibilidade:** Durante a migração, é possível manter `users.profile` e popular `user_perfil` com base nele. O `JwtAuthenticationFilter` pode priorizar rotinas (quando disponíveis) e fallback para o enum.
2. **Performance:** Considerar cache das rotinas por usuário (ex: Redis) se a verificação for feita em toda requisição.
3. **Rotinas dinâmicas vs. estáticas:** Se as rotinas forem fixas no código (endpoints conhecidos), pode-se manter apenas `perfil` e `perfil_rotina`, cadastrando rotinas via seed. Se forem dinâmicas, o CRUD de rotinas é necessário.
