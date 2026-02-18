# Passo a passo: Deploy do frontend Angular na VPS

Guia para publicar o frontend Angular na VPS (ex.: Hostinger), com Docker e Nginx. O frontend fica na porta 80 e as chamadas `/api` são encaminhadas para o backend na porta 8080.

---

## Pré-requisitos

- Backend já rodando na VPS na porta **8080** (ex.: `java -jar` ou serviço).
- **Docker** e **Docker Compose** instalados na VPS.
- Acesso SSH à VPS (ex.: `ssh root@187.77.46.9`).
- Na sua máquina: projeto do repositório (raiz com `docker-compose.yml`, pasta `frontend` com `Dockerfile` e `nginx.docker.conf`).

---

## Passo 1: Preparar o pacote do frontend na sua máquina

No seu computador, dentro da pasta do projeto:

```bash
cd /home/joao/work/fonte/dropshipping
tar --exclude='node_modules' --exclude='dist' --exclude='.angular' -czvf frontend.tar.gz frontend
```

Isso gera o arquivo **`frontend.tar.gz`** na pasta atual (sem `node_modules` nem `dist` para o upload ser rápido).

---

## Passo 2: Enviar o pacote para a VPS

Ainda na sua máquina, substitua `187.77.46.9` pelo IP da sua VPS (e o usuário se não for `root`):

```bash
scp frontend.tar.gz root@187.77.46.9:/home/ubuntu/dev/
```

Se pedir senha, use a senha do usuário da VPS. Se usar chave SSH, o comando é o mesmo.

---

## Passo 3: Conectar na VPS e extrair o frontend

Abra um terminal e conecte via SSH:

```bash
ssh root@187.77.46.9
```

Na VPS, extraia o arquivo e entre na pasta do frontend:

```bash
cd /home/ubuntu/dev
tar -xzvf frontend.tar.gz
cd frontend
```

Confira se existem os arquivos necessários:

```bash
ls -la Dockerfile nginx.conf nginx.docker.conf package.json
```

Todos devem aparecer na listagem.

---

## Passo 4: Liberar a porta 80 no firewall (se usar UFW)

Se a VPS usa UFW, libere a porta 80 (e a 8080 se ainda não estiver liberada):

```bash
sudo ufw allow 80
sudo ufw allow 8080
sudo ufw status
sudo ufw reload
```

---

## Passo 5: Subir o frontend com Docker

Ainda na pasta `frontend` na VPS:

```bash
docker compose up -d --build
```

- O **build** pode levar alguns minutos (instala dependências e compila o Angular).
- Ao terminar, o container **dropshipping-frontend** ficará rodando e escutando na porta **80**.

Para ver os logs (opcional):

```bash
docker compose logs -f
```

Para sair dos logs: `Ctrl+C`.

---

## Passo 6: Verificar se está no ar

1. No navegador, abra: **http://187.77.46.9** (troque pelo IP da sua VPS).
2. A tela de login do frontend deve aparecer.
3. Faça um login de teste: o frontend chama **http://187.77.46.9/api/...** e o Nginx encaminha para o backend na porta 8080.

Se não carregar:

- Confirme se o container está rodando: `docker ps` (deve listar `dropshipping-frontend` na porta 80).
- Confirme se o backend responde: **http://187.77.46.9:8080/swagger-ui/index.html**.

---

## Passo 7: Comandos úteis no dia a dia

| Ação | Comando (dentro de `/home/ubuntu/dev/frontend`) |
|------|--------------------------------------------------|
| Parar o frontend | `docker compose down` |
| Subir de novo | `docker compose up -d` |
| Rebuild após mudanças no código | Enviar novo `frontend.tar.gz`, extrair e rodar `docker compose up -d --build` |
| Ver logs | `docker compose logs -f` |

---

## Resumo dos endereços (exemplo com IP 187.77.46.9)

| Uso | URL |
|-----|-----|
| **Frontend (site)** | http://187.77.46.9 |
| **Backend (API)** | http://187.77.46.9:8080 |
| **Swagger** | http://187.77.46.9:8080/swagger-ui/index.html |

O frontend em produção já usa `apiUrl: '/api'`. Não é necessário alterar o IP no código: o Nginx dentro do container faz o proxy de `/api` para o backend na porta 8080 do host.

---

## Opção alternativa: build na sua máquina e só enviar os arquivos

Se preferir **não** fazer o build na VPS:

1. **Na sua máquina:**  
   ```bash
   cd /home/joao/work/fonte/dropshipping/frontend
   npm ci
   npm run build
   ```  
   A saída fica em **`dist/frontend/browser`**.

2. **Enviar só a pasta de build:**  
   ```bash
   scp -r dist/frontend/browser root@187.77.46.9:/home/ubuntu/dev/frontend-dist/
   ```

3. **Na VPS:** instalar Nginx e configurar um `server` que use `root /home/ubuntu/dev/frontend-dist` e `location /api/` com `proxy_pass http://127.0.0.1:8080/;` (detalhes no guia anterior ou em documentação Nginx + Angular).

O passo a passo principal recomendado é o que usa **Docker** (Passos 1 a 7).
