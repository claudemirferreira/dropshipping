# Passo a passo: Instalação do Docker e Docker Compose no Ubuntu

## Passo 1 – Atualizar o sistema e instalar dependências

```bash
sudo apt update
```

```bash
sudo apt install -y ca-certificates curl gnupg lsb-release
```

---

## Passo 2 – Criar diretório e adicionar chave GPG do Docker

```bash
sudo install -m 0755 -d /etc/apt/keyrings
```

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
```

```bash
sudo chmod a+r /etc/apt/keyrings/docker.gpg
```

---

## Passo 3 – Adicionar repositório do Docker

```bash
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

---

## Passo 4 – Instalar Docker e Docker Compose

```bash
sudo apt update
```

```bash
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

---

## Passo 5 – Adicionar seu usuário ao grupo docker (opcional, para não usar sudo)

```bash
sudo usermod -aG docker $USER
```

**Importante:** faça logout e login novamente (ou reinicie o sistema) para o grupo ter efeito.

---

## Passo 6 – Verificar a instalação

```bash
docker --version
```

```bash
docker compose version
```

---

## Passo 7 – Usar o projeto

```bash
cd /caminho/para/dropshipping
docker compose up -d
```

---

## Resumo rápido (copiar e colar)

Execute cada bloco separadamente, aguardando a conclusão antes do próximo:

```bash
sudo apt update
sudo apt install -y ca-certificates curl gnupg lsb-release
```

```bash
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
```

```bash
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

```bash
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

```bash
sudo usermod -aG docker $USER
```

> Depois do último comando: faça logout e login (ou reinicie) e teste com `docker compose version`.
