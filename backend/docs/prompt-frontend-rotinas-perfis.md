# Prompt: Manter Rotina e Manter Perfil (Frontend Angular)

## Contexto

- **Frontend:** Angular 19, PrimeNG 19, Signals, standalone components
- **Backend:** API REST já implementada em `/api/v1/rotinas` e `/api/v1/perfis`
- **Padrão de referência:** tela de Usuários (`users-list`, `UsersService`)

## Objetivo

Implementar as telas **Manter Rotina** e **Manter Perfil** no frontend, permitindo:

1. **Rotinas:** listar, criar, editar e excluir funcionalidades/rotinas do sistema
2. **Perfis:** listar, criar, editar e excluir perfis, associando rotinas a cada perfil

---

## 1. API Backend (referência)

### Rotinas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/rotinas` | Listar (paginação, filtros: code, name, active) |
| GET | `/api/v1/rotinas/{id}` | Buscar por ID |
| POST | `/api/v1/rotinas` | Criar rotina |
| PUT | `/api/v1/rotinas/{id}` | Atualizar rotina |
| DELETE | `/api/v1/rotinas/{id}` | Excluir rotina |

**CreateRotinaRequest:**
- `code` (string, obrigatório, max 100) – ex: `produtos:listar`
- `name` (string, obrigatório, max 255)
- `description` (string, opcional, max 500)
- `icon` (string, opcional, max 100) – classe CSS, ex: `pi pi-list`
- `path` (string, opcional, max 30) – rota no frontend, ex: `/rotinas`
- `active` (boolean, opcional, default true)

**UpdateRotinaRequest:** mesmos campos do Create.

**RotinaResponse:**
- `id`, `code`, `name`, `description`, `icon`, `path`, `active`, `createdAt`, `updatedAt`

**PageRotinaResponse:** `content`, `totalElements`, `totalPages`, `size`, `number`, `first`, `last`

### Perfis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/perfis` | Listar (paginação, filtros: code, name, active) |
| GET | `/api/v1/perfis/{id}` | Buscar por ID (inclui rotinas) |
| POST | `/api/v1/perfis` | Criar perfil |
| PUT | `/api/v1/perfis/{id}` | Atualizar perfil (e rotinas) |
| DELETE | `/api/v1/perfis/{id}` | Excluir perfil |

**CreatePerfilRequest:**
- `code` (string, obrigatório, max 50) – ex: `ADMIN`
- `name` (string, obrigatório, max 255)
- `description` (string, opcional, max 500)
- `icon` (string, opcional, max 100) – classe CSS, ex: `pi pi-shield`
- `active` (boolean, opcional, default true)
- `rotinaIds` (string[] ou UUID[], opcional) – IDs das rotinas do perfil

**UpdatePerfilRequest:** mesmos campos do Create.

**PerfilResponse:**
- `id`, `code`, `name`, `description`, `icon`, `active`, `rotinas` (RotinaResponse[]), `createdAt`, `updatedAt`

**PagePerfilResponse:** `content`, `totalElements`, `totalPages`, `size`, `number`, `first`, `last`

### Observações

- Endpoints protegidos: `hasAnyRole('ADMIN', 'MANAGER')`
- Base URL: `environment.apiUrl`
- Ordenação padrão na listagem: `sort=name,asc` (page, size, sort nos params)

---

## 2. Serviços

### RotinasService

Criar `frontend/src/app/core/services/rotinas.service.ts`:

- `list(params: ListRotinasParams)` → Observable<PageRotinaResponse>
- `getById(id: string)` → Observable<Rotina>
- `create(data: CreateRotinaRequest)` → Observable<Rotina>
- `update(id: string, data: UpdateRotinaRequest)` → Observable<Rotina>
- `delete(id: string)` → Observable<void>

**Interfaces:**
- `Rotina`: id, code, name, description, icon, path, active, createdAt, updatedAt
- `CreateRotinaRequest`, `UpdateRotinaRequest`: code, name, description?, icon?, path?, active?
- `ListRotinasParams`: code?, name?, active?, page?, size?, sort?

### PerfisService

Criar `frontend/src/app/core/services/perfis.service.ts`:

- `list(params: ListPerfisParams)` → Observable<PagePerfilResponse>
- `getById(id: string)` → Observable<Perfil> (com rotinas)
- `create(data: CreatePerfilRequest)` → Observable<Perfil>
- `update(id: string, data: UpdatePerfilRequest)` → Observable<Perfil>
- `delete(id: string)` → Observable<void>

**Interfaces:**
- `Perfil`: id, code, name, description, icon, active, rotinas: Rotina[], createdAt, updatedAt
- `CreatePerfilRequest`, `UpdatePerfilRequest`: code, name, description?, icon?, active?, rotinaIds?: string[]
- `ListPerfisParams`: code?, name?, active?, page?, size?, sort?

- `listAllRotinas()` → Observable<PageRotinaResponse> ou endpoint sem paginação, se existir, para popular o multiselect de rotinas no formulário de perfil. Caso não exista, usar `list({ size: 500 })` para carregar todas.

---

## 3. Tela Manter Rotina (`rotinas-list`)

### Estrutura (seguir padrão `users-list`)

- **Page header:** título "Rotinas", descrição "Funcionalidades do sistema. Defina quais ações cada perfil pode executar.", badge com total de rotinas
- **Toolbar:** busca por código/nome, filtro de status (Ativo/Inativo/Todos), botão "Nova rotina", botão atualizar
- **Tabela (p-table):** colunas: ícone, código, nome, descrição, path, status, ações
- **Paginação:** igual à de usuários

### Colunas

| Coluna   | Conteúdo |
|----------|----------|
| Ícone    | `<i [class]="row.icon || 'pi pi-circle'"></i>` (se vazio, ícone genérico) |
| Código   | `row.code` |
| Nome     | `row.name` |
| Descrição| `row.description` (truncada) |
| Path     | `row.path` ou "-" |
| Status   | Tag Ativo/Inativo (success/danger) |
| Ações    | Editar, Excluir |

### Dialog de criação/edição

**Campos:**
- Código (obrigatório, placeholder: `produtos:listar`)
- Nome (obrigatório, placeholder: `Listar produtos`)
- Descrição (opcional, textarea)
- Ícone (opcional, placeholder: `pi pi-list`)
- Path (opcional, placeholder: `/produtos`)
- Ativo (checkbox, default true)

**Validações:** code e name obrigatórios. Limites: code 100, name 255, description 500, icon 100, path 30.

---

## 4. Tela Manter Perfil (`perfis-list`)

### Estrutura

- **Page header:** título "Perfis", descrição "Agrupe rotinas em perfis. Usuários recebem acesso conforme os perfis atribuídos.", badge com total de perfis
- **Toolbar:** busca por código/nome, filtro de status (Ativo/Inativo/Todos), botão "Novo perfil", botão atualizar
- **Tabela:** colunas: ícone, código, nome, descrição, qtd. rotinas, status, ações
- **Paginação:** igual à de usuários

### Colunas

| Coluna       | Conteúdo |
|--------------|----------|
| Ícone        | `<i [class]="row.icon || 'pi pi-id-card'"></i>` |
| Código       | `row.code` |
| Nome         | `row.name` |
| Descrição    | `row.description` (truncada) |
| Rotinas      | `row.rotinas?.length ?? 0` |
| Status       | Tag Ativo/Inativo |
| Ações        | Editar, Excluir |

### Dialog de criação/edição

**Campos:**
- Código (obrigatório, placeholder: `ADMIN`)
- Nome (obrigatório, placeholder: `Administrador`)
- Descrição (opcional, textarea)
- Ícone (opcional, placeholder: `pi pi-shield`)
- Ativo (checkbox, default true)
- **Rotinas** – multiselect (p-multiSelect) com lista de todas as rotinas. Opção: label = name, value = id. Carregar rotinas com `RotinasService.list({ size: 500 })` e mapear para `{ label: r.name, value: r.id }`.

**Validações:** code e name obrigatórios. Limites: code 50, name 255, description 500, icon 100.

---

## 5. Navegação e rotas

- Rota **Manter Rotina:** `/rotinas`
- Rota **Manter Perfil:** `/perfis`
- **Menu sidebar:** adicionar itens no `main-layout`:
  - "Rotinas" → `/rotinas`, ícone `pi pi-list`
  - "Perfis" → `/perfis`, ícone `pi pi-id-card`
- **Visibilidade:** exibir itens apenas se o usuário tiver a rotina correspondente (`rotinas:listar`, `perfis:listar`). O backend já envia as rotinas no JWT (authorities). Verificar `auth.currentUser()` ou equivalente e filtrar o menu por `rotinas` ou `authorities` que contenham `rotinas:listar` e `perfis:listar`. Se o layout atual não suportar, exibir ambos para ADMIN/MANAGER por enquanto.

---

## 6. Estrutura de arquivos

```
frontend/src/app/
├── core/
│   └── services/
│       ├── rotinas.service.ts
│       └── perfis.service.ts
├── features/
│   ├── rotinas/
│   │   └── rotinas-list/
│   │       └── rotinas-list.ts
│   └── perfis/
│       └── perfis-list/
│           └── perfis-list.ts
```

---

## 7. Estilo visual

- Manter consistência com Usuários e Produtos:
  - Cards com borda `#e2e8f0`
  - Botão primário verde (`severity="success"`)
  - Paginação com texto/ícones `#5d6c7f`, borda `#dde2e7`
  - Tags para status (success, danger)
  - Modais com fundo claro (estilos globais já aplicados)

---

## 8. Validações e UX

- **Rotinas:** code e name obrigatórios; confirmação antes de excluir
- **Perfis:** code e name obrigatórios; confirmação antes de excluir
- **Toast:** sucesso/erro em create, update, delete
- **Loading** durante requisições
- **Tratamento de erro:** exibir `err.error?.message` do backend quando disponível

---

## 9. Ordem de implementação sugerida

1. Criar `RotinasService` e `PerfisService`
2. Adicionar rotas `/rotinas` e `/perfis` em `app.routes.ts`
3. Adicionar itens no menu sidebar (`main-layout`)
4. Criar `rotinas-list` (tabela, busca, paginação, dialog criar/editar, excluir)
5. Criar `perfis-list` (tabela, busca, paginação, dialog criar/editar com multiselect de rotinas, excluir)

---

## 10. Checklist

### Rotinas
- [ ] RotinasService criado
- [ ] Rota `/rotinas` e menu configurados
- [ ] Listagem com tabela e paginação
- [ ] Filtros: busca, status (ativo/inativo)
- [ ] Dialog de criação com todos os campos
- [ ] Dialog de edição (carregar dados, atualizar)
- [ ] Exclusão com confirmação
- [ ] Toasts e tratamento de erros

### Perfis
- [ ] PerfisService criado
- [ ] Rota `/perfis` e menu configurados
- [ ] Listagem com tabela e paginação
- [ ] Filtros: busca, status
- [ ] Dialog de criação com multiselect de rotinas
- [ ] Dialog de edição com rotinas pré-selecionadas
- [ ] Exclusão com confirmação
- [ ] Toasts e tratamento de erros
