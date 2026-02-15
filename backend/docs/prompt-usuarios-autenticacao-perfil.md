# Prompt para Implementação: Usuários, Autenticação e Perfil

## Contexto do Projeto

- **Stack:** Spring Boot 4.0.2, Java 25, Spring Data JPA, PostgreSQL, Lombok
- **Banco de dados:** PostgreSQL (produção); H2 em memória (testes de integração)
- **Banco local:** Docker Compose para subir PostgreSQL em ambiente de desenvolvimento
- **Versionamento do banco:** Flyway
- **Pacote base:** `com.srv.setebit.dropshipping`
- **Domínio:** Sistema de vendas com controle de produtos, vendas, usuários e comissão de vendedores
- **Arquitetura:** Clean Architecture + DDD
- **Testes:** Testes unitários obrigatórios
- **Documentação da API:** Swagger (OpenAPI 3) via springdoc-openapi

## Objetivo desta Fase

Implementar o módulo de **usuários**, **autenticação** e **perfil**, com foco em segurança da informação.

---

## Arquitetura: Clean Architecture + DDD

### Princípios da Clean Architecture

- **Inversão de dependências:** camadas internas não dependem das externas. O domínio é o núcleo.
- **Regra de dependência:** fluxo de dependência aponta sempre para o domínio (Entities, Use Cases).
- **Independência de frameworks:** o domínio não conhece Spring, JPA ou bibliotecas externas.
- **Testabilidade:** regras de negócio testáveis sem infraestrutura.

### Camadas (de fora para dentro)

1. **Infrastructure / Adapters** – implementações concretas (JPA, controllers REST, JWT)
2. **Application / Use Cases** – orquestração de fluxos, portas de entrada
3. **Domain** – entidades, value objects, regras de negócio, interfaces (portas) de repositórios

### Princípios do DDD

- **Aggregates:** User como Aggregate Root; RefreshToken como entidade dentro do contexto de autenticação.
- **Value Objects:** Email, PasswordHash (não expor senha como string no domínio).
- **Repositories:** interfaces no domínio, implementações na infraestrutura.
- **Domain Services:** lógica que não pertence a uma única entidade (ex: AuthService para login).
- **Use Cases:** cada operação de aplicação mapeada em um Use Case explícito (CreateUserUseCase, LoginUseCase, etc.).

### Regras

- Entidades do domínio sem anotações JPA; mappers convertem entre entidade de domínio e entidade JPA (ou usar JPA entities apenas na infraestrutura, com mapeamento explícito).
- Use Cases recebem portas (interfaces) e retornam DTOs ou entidades de domínio.
- Controllers apenas delegam para Use Cases; sem lógica de negócio em controllers.

---

## 1. Modelo de Dados

### Entidade `User` (usuário do sistema)

- `id` (UUID, chave primária)
- `email` (único, não nulo, índice)
- `passwordHash` – **obrigatório:** senha deve ser criptografada (hash unidirecional com bcrypt ou Argon2) para aumentar a segurança; nunca armazenar em texto plano; nunca expor em APIs
- `name` (nome completo)
- `phone` (opcional)
- `active` (boolean, default true)
- `profile` (enum ou FK para Role/Profile)
- `createdAt`, `updatedAt`
- Auditoria: `createdBy`, `lastModifiedBy` (opcional para v1)

### Perfis/Roles Sugeridos

- `ADMIN` – gestão completa do sistema
- `MANAGER` – gestão de vendas e vendedores
- `SELLER` – vendedor, acesso às próprias vendas e comissões
- `OPERATOR` – operações internas (consultas, relatórios, cadastros auxiliares)

---

## 2. Autenticação

### Requisitos de Segurança

- **Segurança de senhas (obrigatório):** senhas devem ser criptografadas (hash unidirecional) com bcrypt ou Argon2 para aumentar a segurança; nunca armazenar, registrar ou transmitir senhas em texto plano
- **Spring Security 6/7** com autenticação stateless (JWT)
- **JWT:** access token (curta duração, ex.: 15–30 min) + refresh token (longa duração, ex.: 7 dias)
- **Refresh token** persistido em tabela `refresh_token` com possibilidade de revogação
- **Senhas criptografadas (hash):** usar **Bcrypt** ou **Argon2** para hash unidirecional de senha – nunca armazenar em texto plano; cost factor adequado (ex.: Bcrypt strength ≥ 10)
- **Rate limiting** em login (evitar brute force)
- **Proteção contra enumeração:** respostas genéricas em login falho ("credenciais inválidas")
- **CORS** configurado explicitamente
- **CSRF:** desabilitado para APIs stateless; manter proteção se houver sessão/cookies

### Endpoints de Autenticação (versionamento: `/api/v1`)

- `POST /api/v1/auth/login` – retorna access + refresh token
- `POST /api/v1/auth/refresh` – renova access token com refresh token válido; **refresh token rotation:** invalidar o anterior e emitir novo ao usar
- `POST /api/v1/auth/logout` – invalida refresh token
- `GET /api/v1/auth/me` – dados do usuário autenticado (protegido)

### Lockout de conta

- Após **N tentativas falhas** de login (ex.: 5 em 15 min), bloquear temporariamente o usuário ou o IP.
- Permitir recuperação por tempo ou fluxo de reset de senha.

---

## 3. CRUD de Usuários (protegido por role)

### Endpoints (exigir `ADMIN` ou `MANAGER` conforme regra; base path: `/api/v1`)

- `POST /api/v1/users` – criar usuário (senha recebida em texto plano e **imediatamente criptografada** antes de persistir)
- `GET /api/v1/users` – listar com **paginação** (`page`, `size`, `sort`) e filtros (nome, email, profile); padrão: `size=20`, `sort=name,asc`
- `GET /api/v1/users/{id}` – buscar por ID
- `PUT /api/v1/users/{id}` – atualizar (nunca permitir alterar senha por este endpoint)
- `PATCH /api/v1/users/{id}/password` – alterar senha (com validação de senha atual)
- `PATCH /api/v1/users/{id}/activate` e `/deactivate` – ativar/desativar

### Validações

- Bean Validation (Jakarta Validation)
- Email único
- **Política de senha:** mínimo 8 caracteres; pelo menos 1 maiúscula, 1 minúscula, 1 número, 1 caractere especial
- Sanitização de inputs

---

## 4. Perfil do Usuário

- `GET /api/v1/users/me` ou `/api/v1/profile` – dados do usuário logado
- `PUT /api/v1/users/me` ou `/api/v1/profile` – atualizar nome, telefone (não alterar email sem fluxo específico)
- `PATCH /api/v1/profile/password` – alterar própria senha (senha atual + nova senha)

---

## 4.1 Tratamento de Erros e Respostas REST

### Padrão de resposta de erro

- Estrutura sugerida: `{ "timestamp", "status", "error", "message", "path" }` ou RFC 7807 Problem Details.
- Usar `@ControllerAdvice` com `GlobalExceptionHandler` para centralizar o tratamento.

### Mapeamento de exceções para HTTP

| Exceção | HTTP Status |
|---------|-------------|
| `UserNotFoundException` | 404 Not Found |
| `DuplicateEmailException` | 409 Conflict |
| `InvalidCredentialsException` | 401 Unauthorized |
| `InvalidRefreshTokenException` | 401 Unauthorized |
| Acesso negado (role insuficiente) | 403 Forbidden |
| Bean Validation (erros de input) | 400 Bad Request |

---

## 5. Versionamento do Banco com Flyway

### Configuração

- Usar Flyway para migrations
- Dependência: `spring-boot-starter-flyway` (ou `flyway-core` + `flyway-database-postgresql`)
- Scripts em: `src/main/resources/db/migration/`
- Padrão de nomenclatura: `V{versão}__{descrição}.sql` (ex: `V1__create_users_table.sql`)

### Padrão de Migrations Sugerido

- **Importante:** usar tabela `users` ou `app_user` (evitar `user`, palavra reservada no PostgreSQL).

```
db/migration/
├── V1__create_users_table.sql
├── V2__create_refresh_token_table.sql
├── V3__create_indexes.sql           # índices em users(email), refresh_token(token), refresh_token(user_id)
└── V4__insert_initial_roles.sql (se necessário)
```

### Boas Práticas Flyway

- Cada migration deve ser idempotente quando possível
- Não alterar migrations já aplicadas em produção
- Usar transações implícitas do Flyway (um script = uma transação)
- Para alterações em tabelas existentes, criar novas migrations (V5, V6, etc.)

### Configuração Sugerida `application.yaml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/dropshipping
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    validate-on-migrate: true
  jpa:
    hibernate:
      ddl-auto: validate  # NUNCA use create/create-drop em produção; Flyway controla o schema
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
```

**Importante:** Com Flyway, usar `spring.jpa.hibernate.ddl-auto: validate` para que o Hibernate apenas valide o schema, sem criar ou alterar tabelas. O Flyway será responsável por todas as mudanças de schema.

### Docker Compose para PostgreSQL (desenvolvimento local)

- **Objetivo:** subir o banco PostgreSQL via Docker Compose para desenvolvimento e validação local.
- **Arquivo:** `docker-compose.yml` na raiz do projeto.

#### Exemplo `docker-compose.yml`

```yaml
services:
  postgres:
    image: postgres:17-alpine
    container_name: dropshipping-db
    restart: unless-stopped
    environment:
      POSTGRES_DB: dropshipping
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres -d dropshipping"]
      interval: 5s
      timeout: 5s
      retries: 5

volumes:
  postgres_data:
```

#### Uso

```bash
# Subir o banco
docker compose up -d

# Verificar status
docker compose ps

# Parar
docker compose down

# Parar e remover volumes
docker compose down -v
```

#### Variáveis de ambiente (`.env`)

- Criar `.env` na raiz do projeto para credenciais sensíveis; adicionar `.env` ao `.gitignore` se houver senhas reais.
- Exemplo: `POSTGRES_PASSWORD=postgres`; referenciar no `docker-compose.yml` com `${POSTGRES_PASSWORD}`.
- `application.yaml` usa `${DB_USERNAME:postgres}` e `${DB_PASSWORD:postgres}`; o padrão funciona com o exemplo acima.

---

### Banco H2 para Testes de Integração e Validação de Scripts

- **Objetivo:** executar testes de integração e validação de migrations Flyway usando H2 em memória.
- **Perfil:** `test` – ativar com `-Dspring.profiles.active=test` ou `@ActiveProfiles("test")`.
- **Dependência:** `com.h2database:h2` com escopo `test`.

#### Configuração `application-test.yaml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH
    username: sa
    password:
    driver-class-name: org.h2.Driver
  flyway:
    enabled: true
    locations: classpath:db/migration
    validate-on-migrate: true
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
```

**Nota:** `MODE=PostgreSQL` ajuda na compatibilidade de SQL entre PostgreSQL e H2. Evitar funções e tipos específicos do PostgreSQL nas migrations para manter compatibilidade.

#### Validação de Scripts Flyway

- Executar testes de integração com perfil `test` para que o Flyway rode as migrations no H2.
- Garante que os scripts são válidos e que o schema é criado corretamente antes de usar em produção.

---

### Massa de Dados de Teste (um usuário por perfil)

- **Objetivo:** popular o banco em testes com um usuário de cada perfil para validação de roles e fluxos.
- **Local:** `src/test/resources/db/test-data.sql` (executado via `@Sql` ou `data.sql` no perfil test).

#### Usuários a serem criados

- **Senha única para todos (ambiente de teste):** `Senha@123`

| Email | Perfil | Senha (plano) | Nome |
|-------|--------|---------------|------|
| `admin@test.com` | ADMIN | `Senha@123` | Admin Sistema |
| `manager@test.com` | MANAGER | `Senha@123` | Manager Vendas |
| `seller@test.com` | SELLER | `Senha@123` | Vendedor Exemplo |
| `operator@test.com` | OPERATOR | `Senha@123` | Operador Sistema |

#### Estratégia de carga

- **Opção A:** `data.sql` em `src/test/resources/` – Spring Boot executa automaticamente após o schema estar pronto (Flyway).
- **Opção B:** `@Sql(scripts = "/db/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)` nas classes de teste de integração.
- **Opção C:** Migration Flyway apenas para teste – usar `spring.flyway.locations` no perfil test incluindo `classpath:db/migration,classpath:db/testdata` e criar `V99__insert_test_users.sql` em `db/testdata/` (ou pasta equivalente só para teste).

#### Formato sugerido do script

- Inserir usuários com `passwordHash` em bcrypt (gerar hash fixo para ambiente de teste).
- Todos os usuários com `active = true`.
- IDs fixos (UUID) para facilitar asserts nos testes.

**Exemplo (estrutura):**

```sql
-- Senha para todos: Senha@123 (hash bcrypt pré-gerado para ambiente de teste)
INSERT INTO users (id, email, password_hash, name, phone, active, profile, created_at, updated_at)
VALUES
  ('11111111-1111-1111-1111-111111111101', 'admin@test.com', '$2a$10$...', 'Admin Sistema', NULL, true, 'ADMIN', NOW(), NOW()),
  ('11111111-1111-1111-1111-111111111102', 'manager@test.com', '$2a$10$...', 'Manager Vendas', NULL, true, 'MANAGER', NOW(), NOW()),
  ('11111111-1111-1111-1111-111111111103', 'seller@test.com', '$2a$10$...', 'Vendedor Exemplo', NULL, true, 'SELLER', NOW(), NOW()),
  ('11111111-1111-1111-1111-111111111104', 'operator@test.com', '$2a$10$...', 'Operador Sistema', NULL, true, 'OPERATOR', NOW(), NOW())
ON CONFLICT (email) DO NOTHING;
```

- Gerar os hashes bcrypt reais no momento da implementação e substituir no script.

---

## 6. Estrutura de Pacotes Sugerida (Clean Architecture + DDD)

```
com.srv.setebit.dropshipping
├── domain/                          # Camada de Domínio (núcleo)
│   └── user/
│       ├── User.java                # Aggregate Root (entidade de domínio)
│       ├── UserProfile.java         # Value Object / Enum
│       ├── RefreshToken.java        # Entidade (contexto auth)
│       ├── Email.java               # Value Object (opcional)
│       ├── port/
│       │   ├── UserRepositoryPort.java
│       │   └── RefreshTokenRepositoryPort.java
│       └── exception/
│           └── UserNotFoundException.java
│
├── application/                     # Camada de Aplicação (Use Cases)
│   └── user/
│       ├── CreateUserUseCase.java
│       ├── LoginUseCase.java
│       ├── RefreshTokenUseCase.java
│       ├── GetUserByIdUseCase.java
│       ├── UpdateUserUseCase.java
│       ├── ChangePasswordUseCase.java
│       ├── dto/                     # DTOs centralizados aqui; controllers mapeiam para/desde esses DTOs
│       │   ├── request/
│       │   └── response/
│       └── port/
│           ├── PasswordEncoderPort.java
│           └── JwtProviderPort.java
│
├── infrastructure/                  # Camada de Infraestrutura (Adapters)
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── JwtConfig.java
│   │   └── OpenApiConfig.java
│   ├── persistence/
│   │   ├── jpa/
│   │   │   ├── UserJpaEntity.java
│   │   │   ├── RefreshTokenEntity.java
│   │   │   ├── UserJpaRepository.java
│   │   │   └── RefreshTokenRepository.java
│   │   └── adapter/
│   │       ├── UserRepositoryAdapter.java
│   │       └── RefreshTokenRepositoryAdapter.java
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   ├── JwtProviderAdapter.java
│   │   └── BcryptPasswordEncoderAdapter.java
│   └── web/
│       ├── controller/
│       │   ├── AuthController.java
│       │   └── UserController.java
│       └── exception/
│           └── GlobalExceptionHandler.java
│
└── DropshippingApplication.java
```

---

## 7. Swagger (OpenAPI 3)

### Configuração

- **Biblioteca:** `springdoc-openapi-starter-webmvc-ui` (OpenAPI 3 + Swagger UI).
- **Paths:** `/v3/api-docs` (JSON), `/swagger-ui.html` ou `/swagger-ui/index.html` (interface).

### Configuração sugerida por perfil

**`application-dev.yaml` (ou `application.yaml` para dev):**

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    operationsSorter: method
    tagsSorter: alpha
```

**`application-prod.yaml`:**

```yaml
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

### Segurança e Bearer JWT

- Swagger UI deve estar **excluído** da autenticação em ambiente de desenvolvimento.
- Configurar `SecurityFilterChain` para permitir acesso a `/v3/api-docs/**` e `/swagger-ui/**` sem token em dev.
- **OpenApiConfig:** configurar `SecurityScheme` Bearer JWT para permitir testar endpoints protegidos no Swagger UI (botão "Authorize", informar token).
- Exemplo: `addSecurityItem(new SecurityRequirement().addList("bearerAuth"))` e `components(new Components().addSecuritySchemes("bearerAuth", ...))`.

### Boas práticas

- Documentar DTOs com `@Schema` (descrição, exemplo, obrigatório).
- Documentar controllers com `@Operation`, `@ApiResponse`.
- Documentar endpoints de auth (login, refresh) com exemplos de request/response.

---

## 8. Dependências Necessárias

- `spring-boot-starter-security`
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (ou `spring-security-oauth2-resource-server` para JWT)
- `spring-boot-starter-validation`
- `spring-boot-starter-flyway` (ou `flyway-core` + `flyway-database-postgresql`)
- `com.h2database:h2` (escopo `test` – para testes de integração e validação de scripts Flyway)
- `org.springdoc:springdoc-openapi-starter-webmvc-ui` (Swagger / OpenAPI 3)

---

## 9. Testes (Unitários e Integração)

### Testes Unitários

- **Obrigatório:** testes unitários para Use Cases, entidades de domínio (quando houver lógica) e adaptadores críticos.
- **Framework:** JUnit 5 + Mockito (já incluído em `spring-boot-starter-test`).
- **Cobertura:** foco em regras de negócio e fluxos principais; evitar testar código trivial.

### Testes de Integração (H2 + Flyway)

- **Banco:** H2 em memória (perfil `test`).
- **Objetivo:** validar scripts Flyway, endpoints REST e fluxos de autenticação com massa de dados real.
- **Massa de dados:** script `db/test-data.sql` com um usuário de cada perfil (ADMIN, MANAGER, SELLER, OPERATOR).
- **@ActiveProfiles("test")** nas classes de teste de integração.

### O que testar

| Componente | Escopo | Abordagem |
|------------|--------|-----------|
| **Use Cases** | Criação de usuário, login, alteração de senha, ativação/desativação | Mockar portas (repositórios, encoders); validar comportamento esperado e exceções |
| **Domain Entities** | Validações de domínio (se houver) | Testes puros, sem mocks |
| **Value Objects** | Email, PasswordHash (validação, formatação) | Testes puros |
| **Adapters** | UserRepositoryAdapter, JwtProviderAdapter | Mockar JPA repositories / bibliotecas; validar mapeamento correto |

### Padrão sugerido

```
src/test/
├── java/com/srv/setebit/dropshipping/
│   ├── application/
│   │   └── user/
│   │       ├── CreateUserUseCaseTest.java
│   │       ├── LoginUseCaseTest.java
│   │       ├── RefreshTokenUseCaseTest.java
│   │       └── ChangePasswordUseCaseTest.java
│   ├── domain/
│   │   └── user/
│   │       └── UserTest.java (se houver lógica de negócio)
│   ├── infrastructure/
│   │   └── persistence/
│   │       └── adapter/
│   │           └── UserRepositoryAdapterTest.java
│   └── integration/
│       ├── AuthControllerIntegrationTest.java
│       └── UserControllerIntegrationTest.java
└── resources/
    ├── application-test.yaml
    └── db/
        └── test-data.sql
```

### Testes de Integração – Cenários com Massa de Dados

- Senha para todos os usuários de teste: `Senha@123`.
- Login com cada perfil: `admin@test.com`, `manager@test.com`, `seller@test.com`, `operator@test.com`.
- Validação de autorização: ADMIN e MANAGER acessam CRUD de usuários; SELLER e OPERATOR recebem 403 em endpoints restritos.
- Validação de scripts Flyway: execução completa das migrations no H2 durante os testes.

### Boas práticas

- Um teste por comportamento ou cenário (happy path + edge cases + exceções).
- Nomenclatura clara: `deve_[comportamento]_quando_[condição]` ou `when_[condição]_then_[resultado]`.
- Evitar acoplamento com implementação; testar contratos e resultados.
- Use Cases: testar cenários de sucesso, email duplicado, credenciais inválidas, usuário inativo, etc.

### Exemplo de teste de Use Case

```java
@Test
void deve_criar_usuario_quando_dados_validos() {
    // given
    var request = CreateUserRequest.builder()
        .email("user@example.com")
        .password("Senha@123")
        .name("João Silva")
        .profile(UserProfile.SELLER)
        .build();
    when(userRepositoryPort.findByEmail(any())).thenReturn(Optional.empty());
    when(passwordEncoderPort.encode(any())).thenReturn("hashed");

    // when
    var result = createUserUseCase.execute(request);

    // then
    assertThat(result.getEmail()).isEqualTo("user@example.com");
    verify(userRepositoryPort).save(argThat(u -> u.getEmail().equals("user@example.com")));
}

@Test
void deve_lancar_excecao_quando_email_ja_existe() {
    var request = CreateUserRequest.builder().email("exists@example.com").build();
    when(userRepositoryPort.findByEmail(any())).thenReturn(Optional.of(mock(User.class)));

    assertThatThrownBy(() -> createUserUseCase.execute(request))
        .isInstanceOf(DuplicateEmailException.class);
}
```

---

## 10. Checklist de Segurança

- [ ] Senhas **obrigatoriamente criptografadas (hash)** com bcrypt ou Argon2 – nunca em texto plano; cost factor adequado para aumentar a segurança
- [ ] Tokens JWT assinados (HMAC ou RSA)
- [ ] Refresh tokens revogáveis e com TTL
- [ ] Rate limiting em login
- [ ] Proteção contra enumeração de usuários
- [ ] Validação e sanitização de inputs
- [ ] Princípio do menor privilégio por endpoint
- [ ] Headers de segurança (HSTS, X-Content-Type-Options, etc.)
- [ ] Logs sem dados sensíveis (sem senha, sem token completo)

---

## 11. Pré-requisitos e Execução (README)

### Pré-requisitos

- Java 25
- Maven
- Docker e Docker Compose (para banco PostgreSQL local)

### Execução local

```bash
# 1. Subir o banco PostgreSQL
docker compose up -d

# 2. Aguardar o banco estar pronto (healthcheck)

# 3. Executar a aplicação
mvn spring-boot:run

# 4. Acessar Swagger UI: http://localhost:8080/swagger-ui.html
```

### Execução com perfil

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Testes

```bash
mvn test
```

---

## 12. Ordem Sugerida de Implementação

1. Criar `docker-compose.yml` para PostgreSQL na raiz do projeto; criar `.env` (opcional) para credenciais
2. Adicionar dependências (Flyway, Security, Validation, JWT, H2 test, springdoc-openapi) no `pom.xml`
3. Configurar datasource PostgreSQL e Flyway no `application.yaml`
4. Configurar H2 e Flyway no `application-test.yaml` para testes
5. Criar migrations Flyway (tabelas `users`, `refresh_token`, índices em `users(email)`, `refresh_token(token)`, `refresh_token(user_id)`) – compatíveis com PostgreSQL e H2
6. Criar `src/test/resources/db/test-data.sql` com massa de usuários (um por perfil)
7. **Domínio:** entidades, value objects, interfaces (portas) de repositório
8. **Aplicação:** Use Cases (CreateUser, Login, RefreshToken, etc.) e DTOs
9. **Infraestrutura:** entidades JPA, adaptadores de repositório, JwtProviderAdapter, PasswordEncoderAdapter
10. **Infraestrutura:** SecurityConfig, filtros JWT e permissão para Swagger UI em dev
11. **Infraestrutura:** AuthController e UserController (delegando para Use Cases)
12. **Infraestrutura:** OpenApiConfig (Swagger, SecurityScheme Bearer JWT) e documentação dos endpoints com `@Operation`, `@Schema`
13. **Infraestrutura:** `GlobalExceptionHandler` (@ControllerAdvice) para tratamento centralizado de erros
14. **Testes unitários:** Use Cases, entidades de domínio (se houver lógica), adaptadores críticos
15. **Testes de integração:** endpoints de auth e users com H2, massa de dados e validação de roles por perfil
