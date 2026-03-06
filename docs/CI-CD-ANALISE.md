# Análise do CI/CD do projeto

Visão geral do pipeline e do que foi implementado.

---

## O que existe hoje (após melhorias)

### 1. Workflow GitHub Actions

- **Arquivo:** `.github/workflows/deploy-vps.yml` (nome: **CI e Deploy na VPS**)
- **Disparo:** push em `main`, `homolog`, `feature/deploy` ou disparo manual (com opção de pular CI).
- **Concurrency:** um deploy por branch; novo push cancela o anterior em andamento.
- **Jobs:**
  1. **CI (Build e Testes)**  
     Backend: Maven `verify` (build + testes).  
     Frontend: `npm ci` + `npm run build`.  
     Só avança para o deploy se o CI passar (ou se no manual estiver marcado “Pular build/test”).
  2. **Deploy**  
     SSH na VPS → `git pull` → `docker compose up -d --build`.  
     Se o secret `VPS_USE_EXTERNAL_DB` for `true`, usa `docker-compose.external-db.yml` e sobe só **app** e **frontend**.  
     Timeout 15 min; após subir, faz health check em `/actuator/health`.

### 2. Secrets

| Secret | Uso |
|--------|-----|
| `VPS_HOST`, `VPS_USER`, `VPS_SSH_KEY` | Obrigatórios para SSH |
| `VPS_DEPLOY_PATH`, `VPS_ENV_FILE` | Opcionais (defaults: `/root/dev`, `/root/dropshipping.env`) |
| `VPS_USE_EXTERNAL_DB` | Opcional; se `true`, deploy só app + frontend (banco externo) |

### 3. Infra e banco externo

- **application.yaml** – URL do datasource usa `DB_HOST`, `DB_PORT`, `DB_NAME` (defaults: postgres no compose).
- **docker-compose.yml** – Envia `DB_HOST`, `DB_PORT`, `DB_NAME` para o serviço `app` (defaults para o container `postgres`).
- **docker-compose.external-db.yml** – Override que remove `depends_on` do `app` para subir só app + frontend quando o banco é externo.

### 4. Documentação

- **docs/CI-CD-SETUP.md** – Configuração, secrets, banco externo, troubleshooting.
- **docs/CI-CD-ANALISE.md** – Este arquivo.
- **docs/deploy-frontend-vps.md**, **docs/DEPLOY-PASSO-A-PASSO.md** – Deploy manual.

---

## Fluxo resumido

```
Push (main/homolog/feature/deploy) ou Run workflow
    → Concurrency: cancela deploy anterior da mesma branch
    → Job "Build e Testes"
        → Checkout → Java → Maven verify (backend)
        → Node → npm ci → npm run build (frontend)
    → Job "Deploy na VPS" (só se CI passou ou skip_ci=true)
        → SSH na VPS
        → cd $DEPLOY_PATH && git pull
        → Se VPS_USE_EXTERNAL_DB=true: compose com external-db (só app + frontend)
        → Senão: docker compose up -d --build (postgres + app + frontend)
        → Sleep 15s → health check em /actuator/health
```

---

## Pontos de atenção

| Ponto | Situação |
|-------|----------|
| **Imagens/upload** | Volume `app_uploads` na VPS começa vazio em servidor novo. Copiar arquivos para o volume ou usar OCI Object Storage (ver CI-CD-SETUP.md). |
| **API_BASE_URL** | Definir no `.env` da VPS a URL pública do backend para as URLs das imagens. |
| **Deploy ainda é pull-based** | O build de imagem Docker ocorre na VPS; o CI na Actions só valida que o código compila e os testes passam. |
