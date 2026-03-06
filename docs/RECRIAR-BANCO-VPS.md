# Recriar o banco na VPS usando as migrations (Flyway)

Quando você **dropa e recria** o banco (ou o schema) na VPS, as tabelas e dados iniciais são recriados **na próxima vez que a aplicação subir**, porque o Flyway roda as migrations na inicialização.

---

## Opção 1: Só subir a aplicação (recomendado)

Se o banco está **vazio** (recém-criado ou schema `dropshipping` vazio):

1. **Garanta** que o app consegue conectar no banco (variáveis `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` no `.env` da VPS).
2. **Suba os containers** (deploy ou manualmente na VPS):
   ```bash
   cd /root/dev   # ou seu VPS_DEPLOY_PATH
   export ENV_FILE=/root/dropshipping.env
   docker compose --env-file "$ENV_FILE" up -d --build app frontend
   ```
   (Se usar Postgres no próprio compose, use `docker compose --env-file "$ENV_FILE" up -d --build`.)

Na **primeira subida**, o Flyway:

- Cria o schema `dropshipping` (se não existir).
- Executa as migrations em ordem (`V1__create_schema.sql`, `V2__seed_data.sql`, etc.).
- Cria todas as tabelas e insere os dados iniciais (rotinas, perfis, usuários de exemplo).

Não é necessário rodar migration manualmente: **só subir a app** já aplica tudo.

---

## Opção 2: Dropar o schema e recriar do zero

Se o banco já existe e você quer **apagar tudo** e recriar com as migrations:

### No painel da hospedagem (ex.: Hostinger)

1. Abra o gerenciador do PostgreSQL.
2. **Apague** o banco (ou apenas o schema `dropshipping`, se existir).
3. **Crie** um banco novo (vazio) com o mesmo nome que está no `.env` (`DB_NAME`).

### Ou via SSH na VPS (acesso ao `psql`)

Se tiver cliente PostgreSQL na VPS e permissão no banco:

```bash
# Conectar (ajuste usuário, host e nome do banco conforme seu .env)
psql -h $DB_HOST -U $DB_USERNAME -d $DB_NAME -c "DROP SCHEMA IF EXISTS dropshipping CASCADE;"
```

Depois:

1. Suba a aplicação de novo (passo 2 da Opção 1).
2. O Flyway vai criar o schema `dropshipping` e rodar todas as migrations.

---

## Conferir se as migrations rodaram

1. **Logs do backend** (container da app):
   ```bash
   docker compose --env-file /root/dropshipping.env logs app --tail 150
   ```
   Procure por linhas do Flyway, por exemplo: `Migrating schema "dropshipping" to version "1 - create schema"`, `Successfully applied 2 migrations`.

2. **No banco**: verificar se o schema e as tabelas existem (no painel do Hostinger ou via `psql`):
   ```sql
   SET search_path TO dropshipping;
   \dt
   ```

---

## Resumo

| Situação | O que fazer |
|----------|-------------|
| Banco novo/vazio na VPS | Só subir a app (deploy ou `docker compose up`). O Flyway aplica as migrations na subida. |
| Quer recriar tudo | Dropar o schema (ou o banco) e criar de novo; em seguida subir a app. |
| Erro de conexão ao subir | Verificar `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` no `.env` da VPS. |

As migrations estão em `backend/src/main/resources/db/migration/` (por exemplo `V1__create_schema.sql`, `V2__seed_data.sql`). Não é preciso rodá-las à mão na VPS: **só garantir que o banco existe (e está acessível) e subir a aplicação**.
