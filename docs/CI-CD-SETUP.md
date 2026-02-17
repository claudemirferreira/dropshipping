# CI/CD - GitHub Actions

Guia para configurar o deploy automático na VPS via GitHub Actions.

---

## O que o workflow faz

1. Dispara em **push nas branches `main`, `homolog` ou `feature/deploy`** (ou manualmente)
2. Conecta na VPS via **SSH**
3. Atualiza o repositório (`git pull` na branch que disparou)
4. Faz **build e deploy** do backend (Docker Compose)
5. Faz **build e deploy** do frontend (Docker Compose)

---

## Pré-requisitos na VPS

1. **Repositório clonado** no caminho desejado (ex: `/home/ubuntu/dev`)
   ```bash
   git clone https://github.com/SEU_USER/SEU_REPO.git /home/ubuntu/dev
   ```

2. **Docker e Docker Compose** instalados

3. **Arquivo `.env`** em `backend/.env` com as variáveis de produção:
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
| `VPS_USER` | Sim | Usuário SSH (ex: `root` ou `ubuntu`) |
| `VPS_SSH_KEY` | Sim | Conteúdo da chave privada SSH (`cat ~/.ssh/id_rsa`) |
| `VPS_DEPLOY_PATH` | Não | Caminho do repo na VPS (default: `/home/ubuntu/dev`) |

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

Antes do CI/CD funcionar, faça **uma vez** manualmente na VPS:

```bash
cd /home/ubuntu/dev
# Crie backend/.env com as variáveis de produção
cd backend
docker compose -f docker-compose.prod.yml --env-file .env up -d --build

cd ../frontend
docker compose up -d --build
```

Confirme que tudo sobe corretamente. Depois, o GitHub Actions repetirá esse processo em cada push na `main`.

---

## Disparo manual

Em **Actions → Deploy na VPS → Run workflow**, você pode disparar o deploy manualmente sem dar push.

---

## Estrutura esperada na VPS

```
/home/ubuntu/dev/          (ou VPS_DEPLOY_PATH)
├── backend/
│   ├── .env               ← criar manualmente
│   ├── docker-compose.prod.yml
│   ├── Dockerfile
│   └── ...
├── frontend/
│   ├── docker-compose.yml
│   ├── Dockerfile
│   └── ...
└── ...
```

---

## Troubleshooting

| Erro | Solução |
|------|---------|
| `Permission denied (publickey)` | Verifique `VPS_SSH_KEY` e se a chave pública está em `~/.ssh/authorized_keys` na VPS |
| `backend/.env não encontrado` | Crie o arquivo `.env` em `backend/` na VPS |
| `git pull` falha | Confirme que o repo na VPS tem acesso ao GitHub (HTTPS ou SSH com chave deploy) |
| Porta 80 ou 8080 em uso | Pare serviços conflitantes ou ajuste as portas no compose |
