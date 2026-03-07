# Análise do script de deploy (VPS)

Script que roda na VPS via SSH no workflow **CI e Deploy na VPS**.

---

## O que o script faz (ordem)

1. **Variáveis** – Define `DEPLOY_PATH`, `ENV_FILE`, `USE_EXTERNAL_DB` a partir dos secrets (com defaults).
2. **Log** – Imprime início do deploy, path, branch e commit.
3. **Diretório** – `cd` no path do repositório; falha se o diretório não existir.
4. **Git** – `git fetch origin` → `checkout` da branch → `git pull origin <branch>`.
5. **Log** – Mostra o commit atual após o pull.
6. **Docker** – Conforme `USE_EXTERNAL_DB`:
   - `true`: sobe só `app` e `frontend` com override `docker-compose.external-db.yml`.
   - Caso contrário: sobe todos os serviços (`postgres`, `app`, `frontend`).
7. **Espera** – `sleep 15`.
8. **Status** – `docker compose ps -a`.
9. **Health check** – Se o container `dropshipping-app` estiver rodando, testa `http://localhost:8080/actuator/health`.
10. **Log** – Fim do deploy.

---

## Pontos fortes

- **`set -e`** – Qualquer comando que falhar encerra o script (evita seguir após erro).
- **Defaults** – `DEPLOY_PATH` e `ENV_FILE` têm fallback quando os secrets não estão definidos.
- **Branch dinâmica** – Usa `github.ref_name`, então deploya a branch que disparou o workflow.
- **Criação da branch** – Se a branch ainda não existir localmente, usa `git checkout -b <branch> origin/<branch>` após o `fetch`.
- **Health check** – Verificação opcional do backend após subir os containers.
- **Logs** – Início/fim e status dos containers facilitam debug nos logs do Actions.

---

## Riscos e melhorias

| Ponto | Risco | Melhoria |
|-------|--------|----------|
| **git pull** | Se houver alterações locais na VPS (ex.: edição manual), pode dar merge conflict e o script falha. | Usar `git fetch origin` + `git reset --hard origin/<branch>` para deixar o servidor idêntico ao remoto (deploy limpo). |
| **ENV_FILE** | Se o caminho do `.env` estiver errado ou o arquivo não existir, o `docker compose` falha sem aviso claro. | Verificar se o arquivo existe antes de rodar o compose (`[ -f "$ENV_FILE" ]` ou `test -f "$ENV_FILE"`). |
| **Compose com override** | Com `USE_EXTERNAL_DB=true` usamos dois `-f` no `up`, mas no `ps` e no health check usamos só o compose padrão. | Usar os mesmos `-f` (e `--env-file`) nos comandos `ps` e no health check quando `USE_EXTERNAL_DB=true`, para consistência. |
| **Branch com barra** | Nomes como `feature/deploy` são válidos no Git; o script já usa `github.ref_name` corretamente. | Nenhuma mudança necessária. |
| **Timeout** | Build do Docker pode passar de 10 min em servidor lento. | Manter `command_timeout: 10m`; se precisar, aumentar no workflow. |

---

## Sobre o erro `dial tcp ***:22: i/o timeout`

Esse erro ocorre **antes** do script rodar: é falha de **conexão TCP na porta 22** entre o runner do GitHub e a VPS.

- **UFW inativo** na VPS → não é o firewall local que está bloqueando.
- **SSH ativo** → serviço está ouvindo na 22.
- **Conexões bem-sucedidas** de outro IP (ex.: 179.125.160.152) indicam que a VPS aceita SSH.

Conclusão: o timeout tende a ser **rede intermitente** ou **bloqueio no caminho** entre os IPs dos runners do GitHub e a sua VPS (firewall do provedor, rede no meio, etc.). Recomendações:

1. Rodar o workflow de novo (pode ser instabilidade pontual).
2. Confirmar no painel da Hostinger se há restrição de acesso SSH (por IP, região, etc.).
3. Se a Hostinger oferecer IP fixo ou faixa de IPs dos runners, liberar apenas esses IPs (ou, enquanto testa, liberar temporariamente a porta 22 de qualquer origem).

O script em si está correto; o problema é só a conexão SSH até a VPS.
