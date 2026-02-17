# Prompt Task: Integração com Mercado Livre (Meli)

> Documento para orientar a implementação da integração OAuth 2.0 e uso das APIs do Mercado Livre. Use como prompt para IA ou especificação técnica para desenvolvedores.

**Referência oficial:** [Crie uma aplicação no Mercado Livre](https://developers.mercadolivre.com.br/pt_br/crie-uma-aplicacao-no-mercado-livre) · [Autenticação e Autorização](https://developers.mercadolivre.com.br/pt_br/autenticacao-e-autorizacao)

---

## Contexto

**Projeto:** Sistema Dropshipping — Backend Spring Boot 4 (Java 25), Frontend Angular 19.

**Objetivo:** Permitir que o sistema se conecte à conta do vendedor no Mercado Livre (Meli) para, em nome do usuário, publicar/atualizar anúncios, consultar pedidos, mensagens, envios etc., usando OAuth 2.0 (Authorization Code, server-side) e as APIs do Meli.

**Escopo da integração (definir conforme necessidade):**
- **Leitura:** GET (itens, pedidos, usuário, etc.).
- **Escrita:** POST/PUT/DELETE (publicar/editar anúncios, atualizar estoque, etc.).
- **Offline:** uso de `refresh_token` para renovar `access_token` sem o usuário logado (recomendado para automação).

---

## Pré-requisitos (fora do código)

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| P0.1 | Criar aplicação no Mercado Livre | Obrigatório | Acessar [DevCenter](https://developers.mercadolivre.com.br/devcenter) → "Criar uma aplicação". Preencher nome, nome curto, descrição, logo. Usar conta **proprietária** (recomendado: pessoa jurídica). Em alguns países (BR, AR, MX, CL) é permitido **1 aplicação** por titular após validação. |
| P0.2 | Configurar URLs de redirecionamento | Obrigatório | Em "URLs de redirecionamento", cadastrar a URL **exata** que receberá o `code` após autorização (ex.: `https://seu-dominio.com/api/v1/integrations/mercadolivre/callback` ou para dev `https://localhost:4200/integrations/mercadolivre/callback`). **Obrigatório HTTPS** em produção; a URL não pode conter parâmetros variáveis. |
| P0.3 | Habilitar PKCE (recomendado) | Alta | No formulário da aplicação, ativar "Use o PKCE" para proteção contra injeção de código e CSRF. Se ativado, o envio de `code_challenge` e `code_verifier` no fluxo OAuth torna-se obrigatório. |
| P0.4 | Definir escopos | Obrigatório | **Leitura:** GET. **Escrita:** PUT, POST, DELETE. Para ações em nome do usuário sem ele estar online, solicitar **offline_access** (refresh token). Escolher conforme [Permissões funcionais](https://developers.mercadolivre.com.br/pt_br/permissoes-funcionais). |
| P0.5 | Configurar notificações (opcional) | Média | Em "Tópicos", selecionar os desejados (Orders, Messages, Items, Shipments, etc.) e preencher "URL de retorno de notificações" com um endpoint que receberá os POSTs do Meli. |
| P0.6 | Anotar Client ID e Client Secret | Obrigatório | Após criar a aplicação, anotar **Client ID** (APP ID) e **Client Secret** (Secret Key). O Client Secret é **confidencial**; nunca versionar em repositório. Usar variáveis de ambiente ou secret manager. |

---

## Regras gerais de implementação

1. **Segurança:** Nunca expor `client_secret` no frontend; todo o fluxo OAuth (troca de `code` por token, refresh) deve ocorrer no backend.
2. **Redirect URI:** O `redirect_uri` usado nas chamadas deve ser **idêntico** ao cadastrado na aplicação Meli (incluindo protocolo, host, path e ausência de query string variável).
3. **State:** Sempre enviar parâmetro `state` na URL de autorização (valor aleatório seguro) e validar na callback para mitigar CSRF.
4. **Tokens:** Access token expira em **6 horas**; refresh token deve ser guardado e usado **uma única vez** por renovação; o Meli retorna novo refresh token a cada refresh.
5. **Usuário:** O usuário que autorizar deve ser **administrador** da conta; operadores/colaboradores retornam `invalid_operator_user_id` e não podem concluir o grant.
6. Manter compatibilidade com a arquitetura atual: **domain** sem dependências externas; **application** (use cases); **infrastructure** (controllers, clientes HTTP, persistência).

---

## Tarefas por categoria

### 1. Configuração e ambiente

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| C1.1 | Propriedades Meli no backend | Alta | Adicionar em `application.yml` (ou env): `meli.client-id`, `meli.client-secret`, `meli.redirect-uri` (ex.: `https://api.seudominio.com/api/v1/integrations/mercadolivre/callback`). Em dev, usar perfil `dev` com redirect para frontend ou para um endpoint do backend. |
| C1.2 | URLs por ambiente | Alta | Definir `meli.authorization-url` (ex.: `https://auth.mercadolivre.com.br/authorization`) e `meli.token-url` (ex.: `https://api.mercadolibre.com/oauth/token`) — podem ser constantes ou configuráveis por país. |
| C1.3 | Segredo em produção | Alta | Em produção, injetar `client_secret` via variável de ambiente ou secret manager; não deixar em arquivo versionado. |

### 2. Modelo de dados (backend)

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| D2.1 | Entidade/tabela de credenciais Meli | Alta | Persistir por **usuário** (ou por “conta conectada”): `user_id`, `access_token`, `refresh_token`, `expires_at` (ou `expires_in` no momento do refresh), `meli_user_id`. Permite múltiplos vendedores no mesmo sistema. Considerar criptografar tokens em repouso. |
| D2.2 | Migração Flyway | Alta | Criar migration (ex.: `V15__create_mercadolivre_credentials_table.sql`) com tabela e índices (ex.: `user_id` único por conta, `meli_user_id` para consultas). |
| D2.3 | Domínio e portas | Média | Domain: agregado ou entidade `MercadoLivreCredentials` (ou similar); porta `MercadoLivreCredentialsRepositoryPort` para salvar/obter por `userId`. Implementação em infrastructure com JPA. |

### 3. OAuth 2.0 – Fluxo de autorização (backend)

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| O3.1 | Gerar URL de autorização | Alta | Endpoint (ex.: `GET /api/v1/integrations/mercadolivre/authorize`) que: gera `state` (SecureRandom), opcionalmente gera `code_verifier` e `code_challenge` (S256) se PKCE estiver ativo; persiste `state` em cache/sessão associado ao usuário autenticado; redireciona (302) para `https://auth.mercadolivre.com.br/authorization?response_type=code&client_id=...&redirect_uri=...&state=...&code_challenge=...&code_challenge_method=S256`. |
| O3.2 | Callback – troca de code por token | Alta | Endpoint (ex.: `GET` ou `POST` `/api/v1/integrations/mercadolivre/callback`) que: recebe `code` e `state`; valida `state`; troca `code` por token via POST em `https://api.mercadolibre.com/oauth/token` com `grant_type=authorization_code`, `client_id`, `client_secret`, `code`, `redirect_uri` e, se PKCE, `code_verifier`; persiste `access_token`, `refresh_token`, `expires_in`, `user_id` (Meli) na tabela de credenciais associada ao usuário do sistema; redireciona o usuário para a UI (ex.: “Integrações” ou “Conta conectada”). |
| O3.3 | Tratamento de erros na callback | Média | Se Meli retornar `invalid_grant`, `invalid_client`, etc., redirecionar para página de erro ou mensagem amigável (“Não foi possível conectar. Tente novamente.”) e logar detalhes no servidor. |
| O3.4 | Desconectar conta Meli | Média | Endpoint (ex.: `POST /api/v1/integrations/mercadolivre/disconnect`) que remove ou invalida as credenciais do usuário autenticado. |

### 4. Refresh token (backend)

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| R4.1 | Renovar access token | Alta | Ao chamar a API do Meli, verificar se o `access_token` está expirado (ou próximo); se sim, POST em `https://api.mercadolibre.com/oauth/token` com `grant_type=refresh_token`, `client_id`, `client_secret`, `refresh_token`; persistir o novo `access_token` e o **novo** `refresh_token` (o antigo fica inválido). |
| R4.2 | Onde executar o refresh | Alta | Centralizar em um serviço/cliente que sempre usa credenciais válidas: antes de cada chamada à API Meli, ou em job periódico que renova antecipadamente. Evitar múltiplas renovações concorrentes (lock por usuário/conta). |
| R4.3 | Tratamento de refresh falho | Alta | Se retornar `invalid_grant` (ex.: refresh revogado/expirado), marcar credenciais como inválidas e exigir nova autorização pelo usuário (fluxo OAuth desde o passo de autorização). |

### 5. Cliente HTTP para a API Meli (backend)

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| A5.1 | Cliente autenticado | Alta | Implementar cliente (ex.: `MercadoLivreApiClient` ou uso de `WebClient`/`RestTemplate`) que: obtém credenciais do usuário (ou da conta vinculada); aplica refresh se necessário; adiciona header `Authorization: Bearer {access_token}` em todas as requisições para `https://api.mercadolibre.com/...`. |
| A5.2 | Base URL e sites | Média | API base: `https://api.mercadolibre.com`. Para alguns recursos é necessário `site_id` (ex.: MLB para Brasil). Documentar ou configurar site padrão. |
| A5.3 | Tratamento de 401/403 | Alta | Em 401/403, tentar refresh uma vez e reenviar; se persistir, considerar token revogado e solicitar nova conexão. |
| A5.4 | Rate limit (429) | Média | Se Meli retornar `local_rate_limited` (429), implementar backoff e retry conforme [boas práticas](https://developers.mercadolivre.com.br/pt_br/boas-praticas-para-usar-a-plataforma). |

### 6. Use cases e endpoints de aplicação (backend)

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| U6.1 | Iniciar conexão Meli | Alta | Use case que gera state (e code_verifier se PKCE), persiste state, retorna URL de redirecionamento para o frontend chamar (redirect do usuário). |
| U6.2 | Callback – troca code por token | Alta | Use case que recebe code + state, valida state, chama infra para trocar code por token, persiste credenciais, retorna URL de redirecionamento para a UI. |
| U6.3 | Status da integração | Média | Endpoint (ex.: `GET /api/v1/integrations/mercadolivre/status`) que informa se o usuário já conectou a conta Meli e, se quiser, dados básicos (meli_user_id, escopos). |
| U6.4 | (Opcional) Sincronizar itens / pedidos | Baixa | Use cases que consomem o `MercadoLivreApiClient` para listar/criar/atualizar itens ou pedidos; definir conforme necessidade de negócio. |

### 7. Frontend

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| F7.1 | Botão “Conectar Mercado Livre” | Alta | Em tela de integrações (ou configurações), botão que chama `GET /api/v1/integrations/mercadolivre/authorize` e redireciona o usuário para a URL retornada (ou o backend já responde 302 para a URL do Meli). |
| F7.2 | Página de callback | Alta | Rota que o `redirect_uri` aponta (pode ser backend que redireciona de volta ao frontend com query params como `?success=true` ou `?error=...`). Se o callback for no frontend, a página deve enviar o `code` (e `state`) ao backend e depois redirecionar para “Integrações” com mensagem de sucesso ou erro. |
| F7.3 | Exibir status da conexão | Média | Usar `GET /api/v1/integrations/mercadolivre/status` para mostrar “Conectado” ou “Conectar conta” e, se conectado, opção “Desconectar”. |
| F7.4 | Tratar erros e sucesso | Média | Mensagens claras quando a conexão falhar (ex.: redirect_uri não confere, usuário colaborador) ou quando o usuário recusar a autorização no Meli. |

### 8. Notificações (webhooks) – opcional

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| N8.1 | Endpoint de notificações | Média | Endpoint público (ex.: `POST /api/v1/integrations/mercadolivre/notifications`) que recebe os POSTs do Meli conforme [notificações](https://developers.mercadolivre.com.br/pt_br/notificacoes). Validar origem se possível; processar `topic` e `resource` e enfileirar ou processar (pedidos, mensagens, etc.). |
| N8.2 | URL cadastrada no app | Média | A “URL de retorno de notificações” cadastrada na aplicação deve ser esta; HTTPS obrigatório em produção. |

### 9. Testes e documentação

| ID | Tarefa | Prioridade | Detalhes |
|----|--------|------------|----------|
| T9.1 | Testes unitários dos use cases | Alta | Mockar repositório de credenciais e cliente Meli; cobrir fluxo de autorização (URL gerada com state), callback (state inválido, code trocado por token), refresh. |
| T9.2 | Teste de integração (opcional) | Média | Com usuário de teste Meli e app em modo desenvolvimento, testar fluxo completo: redirect → autorizar → callback → persistência; depois chamada à API (ex.: GET `/users/me`) com o token. |
| T9.3 | Documentar endpoints no Swagger | Média | Documentar `authorize`, `callback` (se acessível), `status`, `disconnect` e demais endpoints da integração. |
| T9.4 | README ou runbook | Baixa | Instruções para criar aplicação no Meli, configurar variáveis e testar a conexão em dev. |

---

## Ordem sugerida de implementação

1. **Pré-requisitos:** Criar aplicação no Meli, anotar client_id/client_secret, configurar redirect_uri.
2. **Backend:** Configuração (C1.x) → modelo de dados (D2.x) → fluxo OAuth (O3.x) e use cases (U6.1, U6.2) → refresh (R4.x) → cliente API (A5.x) → status/disconnect (U6.3, O3.4).
3. **Frontend:** Botão e redirect (F7.1) → callback (F7.2) → status e desconectar (F7.3, F7.4).
4. **Opcional:** Notificações (N8.x), sincronização de itens/pedidos (U6.4), testes de integração e documentação (T9.x).

---

## Referências

- [Crie uma aplicação no Mercado Livre](https://developers.mercadolivre.com.br/pt_br/crie-uma-aplicacao-no-mercado-livre)
- [Autenticação e Autorização](https://developers.mercadolivre.com.br/pt_br/autenticacao-e-autorizacao)
- [Permissões funcionais](https://developers.mercadolivre.com.br/pt_br/permissoes-funcionais)
- [API Reference](https://developers.mercadolivre.com.br/pt_br/api-docs-pt-br)
- [Notificações](https://developers.mercadolivre.com.br/pt_br/notificacoes)
- [Realização de testes](https://developers.mercadolivre.com.br/pt_br/realizacao-de-testes) (usuários de teste)
- [Erro 403](https://developers.mercadolivre.com.br/pt_br/erro-403)
- [Recomendações de Autenticação e Token](https://developers.mercadolivre.com.br/pt_br/recomendacoes-de-autenticacao-e-token)
