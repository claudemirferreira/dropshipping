# Deploy completo – Passo a passo

Guia em ordem para colocar a aplicação Dropshipping na VPS (backend + frontend). Use o IP da sua VPS no lugar de `187.77.46.9` onde aparecer.

---

## O que você vai fazer

1. **Na sua máquina:** criar o pacote do frontend e enviar para a VPS.
2. **Na VPS:** Postgres (Docker), backend (Java) e frontend (Docker).
3. **Testar:** acessar o site e a API.

---

## Parte 1 – Na sua máquina

### Passo 1.1 – Ir para a pasta do projeto

O `frontend` fica **na raiz do projeto**, ao lado da pasta `backend`. Use a raiz do repositório:

```bash
cd /home/joao/work/fonte/dropshipping
```

Confira se a pasta `frontend` existe:

```bash
ls -d frontend
```

Deve mostrar: `frontend`.

---

### Passo 1.2 – Criar o pacote do frontend

Ainda em `/home/joao/work/fonte/dropshipping`:

```bash
tar --exclude='node_modules' --exclude='dist' --exclude='.angular' -czvf frontend.tar.gz frontend
```

Ao terminar, deve existir o arquivo **`frontend.tar.gz`** nessa mesma pasta.

---

### Passo 1.3 – Enviar o pacote para a VPS

Substitua `187.77.46.9` pelo IP da sua VPS e `root` pelo usuário, se for outro:

```bash
scp frontend.tar.gz root@187.77.46.9:/home/ubuntu/dev/
```

Digite a senha (ou use chave SSH) quando pedir.

---

## Parte 2 – Na VPS

Conecte na VPS:

```bash
ssh root@187.77.46.9
```

Os passos abaixo são **todos na VPS**, em sequência.

---

### Passo 2.1 – Criar a pasta (se não existir)

```bash
mkdir -p /home/ubuntu/dev
cd /home/ubuntu/dev
```

---

### Passo 2.2 – Extrair o frontend

Se você acabou de enviar o `frontend.tar.gz`:

```bash
cd /home/ubuntu/dev
tar -xzvf frontend.tar.gz
```

Deve aparecer a pasta **`frontend`**. Confira:

```bash
ls -la frontend/Dockerfile frontend/nginx.conf frontend/docker-compose.yml
```

---

### Passo 2.3 – Firewall (portas 80 e 8080)

Se a VPS usa UFW:

```bash
ufw allow 80
ufw allow 8080
ufw reload
ufw status
```

---

### Passo 2.4 – Postgres (Docker)

Se o banco ainda não estiver rodando, use o compose só do banco. Na pasta onde está o `docker-compose.db.yml` (por exemplo `/home/ubuntu/dev`):

```bash
cd /home/ubuntu/dev
docker compose -f docker-compose.db.yml up -d
```

Verifique:

```bash
docker ps
```

Deve haver um container tipo `dropshipping-db` na porta 5432.

(Opcional) Criar o schema usado pelo backend:

```bash
docker exec -it dropshipping-db psql -U postgres -d dropshipping -c "CREATE SCHEMA IF NOT EXISTS dropshipping;"
```

---

### Passo 2.5 – Backend (Java)

O backend precisa estar rodando na porta **8080** (por exemplo com `java -jar` ou systemd). Exemplo com JAR na pasta `backend`:

```bash
cd /home/ubuntu/dev/backend
java -jar target/dropshipping-*.jar
```

Ou em segundo plano (nohup):

```bash
cd /home/ubuntu/dev/backend
nohup java -jar target/dropshipping-*.jar > backend.log 2>&1 &
```

Confirme que a API responde:

```bash
curl -s -o /dev/null -w "%{http_code}" http://127.0.0.1:8080/swagger-ui/index.html
```

Deve retornar `200` (ou 302). No navegador: **http://187.77.46.9:8080/swagger-ui/index.html**.

---

### Passo 2.6 – Frontend (Docker)

Build e subida do container do frontend (Nginx + Angular):

```bash
cd /home/ubuntu/dev/frontend
docker compose up -d --build
```

O primeiro build pode levar alguns minutos. Ao terminar:

```bash
docker ps
```

Deve aparecer **dropshipping-frontend** na porta 80.

---

## Parte 3 – Verificar

### No navegador

| O quê        | URL |
|-------------|-----|
| **Site (frontend)** | http://187.77.46.9 |
| **API (backend)**   | http://187.77.46.9:8080 |
| **Swagger**         | http://187.77.46.9:8080/swagger-ui/index.html |

1. Abra **http://187.77.46.9** → deve carregar a tela do frontend (ex.: login).
2. Faça login → as chamadas vão para **http://187.77.46.9/api/...** e o Nginx encaminha para o backend na porta 8080.

---

## Resumo rápido (já com tudo na VPS)

**Na sua máquina (uma vez por deploy do frontend):**

```bash
cd /home/joao/work/fonte/dropshipping
tar --exclude='node_modules' --exclude='dist' --exclude='.angular' -czvf frontend.tar.gz frontend
scp frontend.tar.gz root@187.77.46.9:/home/ubuntu/dev/
```

**Na VPS:**

```bash
ssh root@187.77.46.9
cd /home/ubuntu/dev
tar -xzvf frontend.tar.gz
cd frontend
docker compose up -d --build
```

Substitua **187.77.46.9** pelo IP da sua VPS em todos os comandos e URLs.
