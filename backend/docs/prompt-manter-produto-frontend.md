# Prompt: Manter Produto (Frontend Angular)

## Contexto

- **Frontend:** Angular 19, PrimeNG 19, Signals, standalone components
- **Backend:** API REST já implementada em `/api/v1/products` e `/api/v1/products/{id}/images`
- **Padrão de referência:** tela de Usuários (`users-list`, `UsersService`)

## Objetivo

Implementar a tela de **manter produto** (CRUD) no frontend, permitindo **cadastrar várias fotos** por produto.

---

## 1. API Backend (referência)

### Produtos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/products` | Listar (paginação, filtros: name, status, categoryId) |
| GET | `/api/v1/products/{id}` | Buscar por ID (retorna `ProductDetailResponse` com imagens) |
| POST | `/api/v1/products` | Criar produto (pode incluir `images[]` no body) |
| PUT | `/api/v1/products/{id}` | Atualizar produto |
| PATCH | `/api/v1/products/{id}/status` | Alterar status |
| DELETE | `/api/v1/products/{id}` | Excluir produto |

### Fotos do produto

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/v1/products/{id}/images` | Listar imagens (ordenadas por position) |
| POST | `/api/v1/products/{id}/images` | Adicionar imagem |
| PUT | `/api/v1/products/{id}/images/{imageId}` | Atualizar imagem (position, isMain, altText) |
| DELETE | `/api/v1/products/{id}/images/{imageId}` | Remover imagem |

### DTOs principais

**ProductResponse (listagem):**
- id, sku, name, shortDescription, salePrice, costPrice, currency, status, slug, mainImageUrl, createdAt, updatedAt

**ProductDetailResponse (detalhe):**
- Todos os campos do produto + `images: ProductImageResponse[]`

**ProductImageResponse:**
- id, url, position, main, altText

**CreateProductRequest:**
- sku, name, shortDescription, fullDescription, salePrice, costPrice, currency, status
- supplierSku, supplierName, supplierProductUrl, leadTimeDays, isDropship
- weight, length, width, height
- slug, categoryId, brand, metaTitle, metaDescription
- compareAtPrice, stockQuantity
- **images: [{ url, position, isMain, altText }]**

**CreateProductImageRequest:**
- url, position, isMain, altText

---

## 2. Serviço `ProductsService`

Criar `frontend/src/app/core/services/products.service.ts`:

- `list(params)` → Observable<PageProductResponse>
- `getById(id)` → Observable<ProductDetailResponse>
- `create(data: CreateProductRequest)` → Observable<ProductDetailResponse>
- `update(id, data: UpdateProductRequest)` → Observable<ProductDetailResponse>
- `updateStatus(id, status)` → Observable<ProductDetailResponse>
- `delete(id)` → Observable<void>
- `addImage(productId, data)` → Observable<ProductImageResponse>
- `updateImage(productId, imageId, data)` → Observable<ProductImageResponse>
- `removeImage(productId, imageId)` → Observable<void>

Usar `environment.apiUrl` e `HttpClient` com interceptor de auth.

---

## 3. Tela de listagem `products-list`

### Estrutura (seguir padrão `users-list`)

- **Page header:** título "Produtos", descrição, badge com total de produtos
- **Toolbar:** busca por nome/SKU, filtro de status (dropdown), botão "Novo produto", botão atualizar
- **Tabela (p-table):** colunas: imagem (thumbnail), SKU, nome, preço venda, status, ações
- **Paginação:** igual à de usuários (cores #5d6c7f, borda #dde2e7)

### Ações na tabela

- **Editar** – abre dialog de edição
- **Alterar status** – dropdown ou botões Ativar/Inativar
- **Excluir** – ConfirmationService antes de deletar

### Dialog de criação/edição

**Aba ou seções:**
1. **Identificação:** sku, name, shortDescription, fullDescription, slug
2. **Preços:** salePrice, costPrice, compareAtPrice, currency
3. **Fornecedor:** supplierSku, supplierName, supplierProductUrl, leadTimeDays, isDropship
4. **Dimensões:** weight, length, width, height (opcionais)
5. **Categorização:** categoryId, brand (opcionais na v1)
6. **SEO:** metaTitle, metaDescription (opcionais)
7. **Status:** status (dropdown: DRAFT, ACTIVE, INACTIVE, OUT_OF_STOCK)
8. **Fotos** – **seção principal para várias fotos**

---

## 4. Gestão de várias fotos (seção obrigatória)

### Regras

- Um produto pode ter **várias fotos**
- Cada foto tem: **url** (obrigatória), **position**, **isMain** (uma deve ser principal), **altText**
- Pelo menos **uma foto principal** (`isMain = true`)

### UI sugerida

- **Lista de fotos** já cadastradas: cards ou lista com preview (img), ordem (position), indicador "Principal", botões Editar ordem / Definir como principal / Remover
- **Adicionar nova foto:** formulário inline (campo URL, checkbox "É principal", altText); botão "Adicionar foto"
- **Ordenação:** drag-and-drop (opcional) ou campos numéricos para position
- **Na criação:** enviar `images` no `CreateProductRequest`
- **Na edição:** usar endpoints de imagens separados (POST/PUT/DELETE em `/images`) ou enviar lista completa conforme API

### Fluxo na criação

1. Usuário preenche dados do produto
2. Adiciona fotos na lista local (URL + isMain + altText)
3. Ao salvar: `POST /products` com `images: [...]`
4. Se nenhuma foto tiver isMain, definir a primeira como main

### Fluxo na edição

1. Carregar produto com `getById(id)` – já vem com `images`
2. Listar fotos; permitir adicionar (POST /images), remover (DELETE /images/{id}), alterar ordem/main (PUT /images/{id})
3. Ou: formulário com lista editável e ao salvar sincronizar diferenças (mais complexo)

### Opção simplificada (v1)

- **Criação:** formulário com lista de imagens (array no form). Cada item: url, isMain, altText. Adicionar/remover na lista. Enviar tudo no POST.
- **Edição:** carregar imagens; exibir lista com opção de adicionar (POST) e remover (DELETE). Para reordenar/alterar main: PUT em cada imagem alterada.

---

## 5. Validações e UX

- **Campos obrigatórios:** sku, name, shortDescription, salePrice, costPrice, currency, status, slug
- **Slug:** gerado automaticamente a partir do nome (opcional) ou editável
- **Toast:** sucesso/erro em create, update, delete
- **Confirmação** antes de excluir produto
- **Loading** durante requisições

---

## 6. Navegação e rotas

- Rota: `/produtos`
- Menu sidebar: item "Produtos" com ícone `pi-box` ou `pi-shopping-bag`
- Auth guard: mesma proteção das demais rotas

---

## 7. Estilo visual

- Manter consistência com a tela de Usuários:
  - Cards com borda #e2e8f0
  - Botão primário verde (#22c55e)
  - Paginação com texto/ícones #5d6c7f, borda #dde2e7
  - Tags para status (success, danger, warning, info)

---

## 8. Ordem de implementação sugerida

1. Criar `ProductsService` com todos os métodos
2. Adicionar rota `/produtos` e item no menu
3. Criar `products-list` com tabela, busca, paginação (sem dialog)
4. Implementar dialog de criação com formulário completo + **seção de fotos (lista de URLs)**
5. Implementar dialog de edição (carregar dados, usar endpoints de imagens)
6. Implementar exclusão com confirmação
7. Ajustar status (ativar/inativar) e filtros

---

## 9. Checklist

- [ ] ProductsService criado
- [ ] Rota e menu configurados
- [ ] Listagem com tabela e paginação
- [ ] Dialog de criação com todas as seções
- [ ] **Seção de fotos: adicionar várias fotos por URL**
- [ ] Pelo menos uma foto marcada como principal
- [ ] Edição de produto e fotos
- [ ] Exclusão com confirmação
- [ ] Toasts e tratamento de erros
