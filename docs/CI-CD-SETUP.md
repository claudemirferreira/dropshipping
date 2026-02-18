# CI/CD - GitHub Actions

Guia para configurar o deploy automático na VPS via GitHub Actions.

---

## O que o workflow faz

1. Dispara em **push nas branches `main`, `homolog` ou `feature/deploy`** (ou manualmente)
2. Conecta na VPS via **SSH**
3. Atualiza o repositório (`git pull` na branch que disparou)
4. Faz **build e deploy** completo: Postgres + Backend + Frontend (um único `docker compose`)

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
   - `DB_USERNAME`, `DB_PASSWORD`
   - `JWT_SECRET`
   - `MAIL_FROM`, `MAIL_PASSWORD`, etc.
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

## Disparo manual

Em **Actions → Deploy na VPS → Run workflow**, você pode disparar o deploy manualmente sem dar push.

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

## Troubleshooting

| Erro | Solução |
|------|---------|
| `Permission denied (publickey)` | Verifique `VPS_SSH_KEY` e se a chave pública está em `~/.ssh/authorized_keys` na VPS |
| `.env não encontrado` | Crie o arquivo `/root/dropshipping.env` na VPS |
| `git pull` falha | Confirme que o repo na VPS tem acesso ao GitHub (HTTPS ou SSH com chave deploy) |
| Porta 80 ou 8080 em uso | Pare serviços conflitantes ou ajuste as portas no compose |
