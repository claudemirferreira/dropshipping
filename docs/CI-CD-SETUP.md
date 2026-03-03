# CI/CD - GitHub Actions

Guia para configurar o deploy automático na VPS via GitHub Actions.

- **Workflow:** `.github/workflows/deploy-vps.yml`
- **Análise do pipeline:** [CI-CD-ANALISE.md](CI-CD-ANALISE.md)
- **Recriar o banco na VPS (migrations):** [RECRIAR-BANCO-VPS.md](RECRIAR-BANCO-VPS.md)

---

## O que o workflow faz

1. **CI (Build e Testes)** – Roda antes do deploy:
   - Build e testes do backend (Maven `verify`)
   - Build do frontend (npm ci + build)
   - O deploy só roda se o CI passar.
2. **Deploy** – Só executa após o CI (ou se você escolher "Pular build/test" no disparo manual):
   - Conecta na VPS via **SSH**
   - Atualiza o repositório (`git pull`)
   - Sobe os containers com `docker compose` (ou só app + frontend se usar banco externo).
   - Faz uma checagem de saúde do backend (`/actuator/health`) após subir.

**Disparo:** push em `main`, `homolog` ou `feature/deploy`, ou manual em **Actions → CI e Deploy na VPS → Run workflow** (com opção de pular CI).

---

## Pré-requisitos na VPS

1. **Repositório clonado** (ex: `/root/dev`)
   ```bash
   mkdir -p /root/dev
   cd /root/dev
   git clone https://github.com/claudemirferreira/dropshipping.git .
   ```

2. **Docker e Docker Compose** instalados

3. **Arquivo de variáveis** em `/root/dropshipping.env` (fora do repositório):
   - `DB_USERNAME`, `DB_PASSWORD` (ou `DB_HOST`, `DB_PORT`, `DB_NAME` se o banco for externo, ex.: Hostinger)
   - `JWT_SECRET`
   - `MAIL_FROM`, `MAIL_PASSWORD`, etc.
   - `API_BASE_URL` = URL pública do backend (para as URLs das imagens).
   - Nunca commite o `.env` no repositório.

4. **Chave SSH** configurada para deploy:
   - O usuário da VPS deve aceitar conexão SSH com chave
   - A chave privada será usada como secret no GitHub

---

## Secrets no GitHub

Em **Settings → Secrets and variables → Actions**, crie:

| Secret | Obrigatório | Descrição |
|--------|-------------|-----------|
| `VPS_HOST` | Sim | IP ou hostname da VPS (ex: `187.77.46.9`) |
| `VPS_USER` | Sim | Usuário SSH (ex: `root`) |
| `VPS_SSH_KEY` | Sim | Conteúdo da chave privada SSH (`cat ~/.ssh/id_ed25519`) |
| `VPS_DEPLOY_PATH` | Não | Caminho do repo na VPS (default: `/root/dev`) |
| `VPS_ENV_FILE` | Não | Caminho do .env (default: `/root/dropshipping.env`) |
| `VPS_USE_EXTERNAL_DB` | Não | Se for `true`, sobe só app + frontend (banco externo). No `.env` da VPS defina `DB_HOST`, `DB_PORT`, `DB_NAME`. |

### Como obter o conteúdo da chave SSH

Na sua máquina:

```bash
cat ~/.ssh/id_rsa
```

Copie **todo** o conteúdo (incluindo `-----BEGIN ... KEY-----` e `-----END ... KEY-----`) e cole no secret `VPS_SSH_KEY`.

Se não tiver chave, crie uma sem senha:

```bash
ssh-keygen -t ed25519 -C "github-actions" -f ~/.ssh/github_actions -N ""
```

Depois adicione a chave pública na VPS:

```bash
ssh-copy-id -i ~/.ssh/github_actions.pub root@187.77.46.9
```

E use o conteúdo de `~/.ssh/github_actions` no secret.

---

## Primeiro deploy manual na VPS

Antes do CI/CD funcionar, faça **uma vez** na VPS:

```bash
# 1. Clone o repo
mkdir -p /root/dev && cd /root/dev
git clone https://github.com/claudemirferreira/dropshipping.git .

# 2. Crie o arquivo de variáveis (fora do repo)
nano /root/dropshipping.env
# Cole DB_PASSWORD, JWT_SECRET, MAIL_*, etc.

# 3. Suba tudo
export ENV_FILE=/root/dropshipping.env && docker compose --env-file $ENV_FILE up -d --build
```

---

## Migração da estrutura antiga

Se você já tinha `backend/.env` na VPS:

```bash
# Copiar .env para fora do repo
cp /root/dev/backend/.env /root/dropshipping.env

# Parar containers antigos (se existirem)
docker stop dropshipping-frontend dropshipping-app dropshipping-db 2>/dev/null || true
```

---

## Como saber se o deploy rodou na VPS

### 1. No GitHub Actions

1. Abra o repositório no GitHub → **Actions**.
2. Clique na execução do workflow **"CI e Deploy na VPS"** (a mais recente).
3. Confira:
   - **Build e Testes** – se estiver verde, o CI passou.
   - **Deploy na VPS** – se estiver verde, o passo de SSH executou até o fim.
4. Dentro de **Deploy na VPS**, clique em **"Deploy na VPS via SSH"** e abra o log. Você deve ver:
   - `========== INÍCIO DO DEPLOY NA VPS ==========`
   - O path e o arquivo de env usados.
   - `Commit atual no servidor após pull: <hash>`
   - `========== STATUS DOS CONTAINERS ==========` e a lista de containers.
   - `========== FIM DO DEPLOY NA VPS ==========`

Se o job **Deploy na VPS** estiver **vermelho (falhou)**, o deploy não concluiu: leia a mensagem de erro nesse job (geralmente falha de SSH, `git pull` ou `docker compose`).

Se o job **Build e Testes** falhar, o **Deploy na VPS** nem chega a rodar (deploy só executa após o CI passar).

### 2. Direto na VPS (SSH)

Conecte na VPS e rode:

```bash
# Caminho onde está o projeto (ajuste se usar outro)
cd /root/dev   # ou o valor de VPS_DEPLOY_PATH

# Último commit deployado (deve bater com o que está no GitHub na branch)
git log -1 --oneline

# Containers em execução (devem aparecer dropshipping-app e dropshipping-frontend)
docker ps

# Se algo falhou, logs do backend
docker compose --env-file /root/dropshipping.env logs app --tail 100
```

Se `docker ps` não mostrar os containers ou o último `git log` estiver desatualizado em relação ao GitHub, o deploy não está rodando ou está falhando (veja o log do job no Actions).

### 3. Resumo rápido

| Onde ver | O que significa |
|----------|------------------|
| Actions → job "Deploy na VPS" verde | O script de deploy rodou na VPS até o fim. |
| Actions → "Deploy na VPS" vermelho | Erro no deploy (SSH, git, docker ou env); leia o log do job. |
| Actions → "Build e Testes" vermelho | Deploy nem inicia; corrija o código e rode de novo. |
| Na VPS: `docker ps` vazio | Containers não subiram ou caíram (veja `docker compose logs`). |
| Na VPS: `git log -1` antigo | O `git pull` não está trazendo o commit novo (branch errada ou erro no pull). |

---

## Disparo manual

Em **Actions → CI e Deploy na VPS → Run workflow** você pode:
- Rodar o pipeline manualmente (CI + deploy).
- Marcar **"Pular build/test e ir direto ao deploy"** para subir direto na VPS sem rodar testes (use só se confiar no código já commitado).

---

## Estrutura esperada na VPS

```
/root/
├── dropshipping.env       ← variáveis (fora do repo)
└── dev/                   (repositório)
    ├── docker-compose.yml
    ├── backend/
    ├── frontend/
    └── ...
```

---

## Banco externo (ex.: Hostinger)

Se o PostgreSQL for no painel da hospedagem (não no Docker):

1. No **GitHub → Settings → Secrets**, crie o secret `VPS_USE_EXTERNAL_DB` com valor `true`.
2. No `.env` da VPS (`/root/dropshipping.env`), defina:
   - `DB_HOST` = host do PostgreSQL (ex.: o que o painel informar)
   - `DB_PORT` = 5432 (ou a porta informada)
   - `DB_NAME` = nome do banco
   - `DB_USERNAME` e `DB_PASSWORD`
3. O workflow sobe apenas os containers **app** e **frontend** (usa `docker-compose.external-db.yml`).

---

## Troubleshooting

| Erro | Solução |
|------|---------|
| `Permission denied (publickey)` | Verifique `VPS_SSH_KEY` e se a chave pública está em `~/.ssh/authorized_keys` na VPS |
| `.env não encontrado` | Crie o arquivo `/root/dropshipping.env` na VPS |
| `git pull` falha | Confirme que o repo na VPS tem acesso ao GitHub (HTTPS ou SSH com chave deploy) |
| Porta 80 ou 8080 em uso | Pare serviços conflitantes ou ajuste as portas no compose |
| Connection to localhost:5432 refused | Use banco externo: defina `DB_HOST`, `DB_PORT`, `DB_NAME` no `.env` e o secret `VPS_USE_EXTERNAL_DB=true` |
| CI falha (build ou testes) | Corrija o código; o deploy só roda se o CI passar (a menos que use "Pular build/test" no disparo manual) |
