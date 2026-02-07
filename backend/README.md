# Dropshipping - Sistema de Vendas

Sistema de vendas com controle de produtos, vendas, usuários e comissão de vendedores.

## Pré-requisitos

- Java 25
- Maven
- Docker e Docker Compose (para banco PostgreSQL local)
  - **Ubuntu:** [docs/instalacao-docker-compose-ubuntu.md](docs/instalacao-docker-compose-ubuntu.md)

## Execução local

```bash
# 1. Subir o banco PostgreSQL
docker compose up -d

# 2. Aguardar o banco estar pronto (healthcheck)

# 3. Executar a aplicação
mvn spring-boot:run

# 4. Acessar Swagger UI: http://localhost:8080/swagger-ui.html
```

## Carga de usuário para testar endpoints (dev)

Para criar um usuário admin na primeira subida e testar login/endpoints:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

Ou no IDE: ative o perfil **dev** na configuração de run.

**Usuário criado (apenas se ainda não existir):**
- **Email:** admin@dropshipping.com  
- **Senha:** Senha@123  
- **Perfil:** ADMIN  

Use esse usuário no Swagger para fazer login (`POST /api/v1/auth/login`) e testar os demais endpoints.

## Testes

```bash
mvn test
```

## Usuários de teste (perfil test / integração)

- Email: admin@test.com, manager@test.com, seller@test.com, operator@test.com
- Senha: Senha@123


{
"email": "claudemirramosferreira@gmail.com",
"password": "Senha@123",
"name": "João Silva",
"phone": "string",
"profile": "ADMIN"
}
