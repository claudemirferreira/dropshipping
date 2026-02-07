# Prompt para Implementação: Produtos (Catálogo Dropshipping)

## Contexto do Projeto

- **Stack:** Spring Boot 4.0.2, Java 25, Spring Data JPA, PostgreSQL, Lombok
- **Banco de dados:** PostgreSQL (produção); H2 em memória (testes de integração)
- **Pacote base:** `com.srv.setebit.dropshipping`
- **Domínio:** Sistema de dropshipping com catálogo de produtos expostos no site
- **Arquitetura:** Clean Architecture + DDD

## Objetivo desta Fase

Implementar o módulo de **produtos** para o catálogo que será exposto no site, incluindo atributos essenciais para vendas dropshipping.

---

## 1. Modelo de Dados – Entidade `Product`

### Atributos Obrigatórios

| Atributo       | Tipo          | Descrição                                      |
|----------------|---------------|------------------------------------------------|
| `id`           | UUID          | Chave primária                                 |
| `sku`          | String        | Código único do produto (interno ou fornecedor)|
| `name`         | String        | Nome do produto                                |
| `shortDescription` | String    | Descrição curta (listagens, cards)             |
| `fullDescription`  | String/Text | Descrição completa (página do produto)         |
| `salePrice`    | BigDecimal    | Preço de venda                                 |
| `costPrice`    | BigDecimal    | Custo (preço do fornecedor)                     |
| `currency`     | String        | Moeda (ex: BRL, USD) – default BRL             |
| `status`       | Enum          | `ACTIVE`, `INACTIVE`, `OUT_OF_STOCK`, `DRAFT`  |
| `createdAt`    | Instant       | Data de criação                                |
| `updatedAt`    | Instant       | Data de atualização                             |

### Atributos de Dropshipping

| Atributo          | Tipo     | Descrição                                      |
|-------------------|----------|------------------------------------------------|
| `supplierSku`     | String   | SKU do fornecedor (opcional)                   |
| `supplierName`    | String   | Nome do fornecedor                             |
| `supplierProductUrl` | String| URL do produto no site do fornecedor           |
| `leadTimeDays`    | Integer  | Prazo de envio do fornecedor (dias)            |
| `isDropship`      | Boolean  | Se é produto dropship (default true)           |

### Atributos para Frete e Dimensões

| Atributo      | Tipo       | Descrição                                      |
|---------------|------------|------------------------------------------------|
| `weight`      | BigDecimal | Peso (kg)                                      |
| `length`      | BigDecimal | Comprimento (cm)                               |
| `width`       | BigDecimal | Largura (cm)                                   |
| `height`      | BigDecimal | Altura (cm)                                    |

### Atributos de Categorização e SEO

| Atributo        | Tipo   | Descrição                                      |
|-----------------|--------|------------------------------------------------|
| `slug`          | String | URL amigável (único, ex: `camiseta-basica-preta`)|
| `categoryId`    | UUID   | FK para categoria (opcional na v1)             |
| `brand`         | String | Marca (opcional)                               |
| `metaTitle`     | String | Título para SEO (opcional)                     |
| `metaDescription` | String| Descrição para meta tags SEO                   |

### Atributos de Mídia (via relação 1:N com `ProductImage`)

- Um produto possui **várias fotos**; cada foto é persistida na entidade `ProductImage` (tabela separada).
- Removido `mainImageUrl` e `imageUrls` – substituídos pela entidade `ProductImage` abaixo.

### Atributos Opcionais / Variações

| Atributo        | Tipo   | Descrição                                      |
|-----------------|--------|------------------------------------------------|
| `compareAtPrice`| BigDecimal | Preço “de” (riscado) – ex: de R$ 99 por R$ 79 |
| `stockQuantity` | Integer | Estoque (se controlar internamente; em dropship pode ser 0 ou ilimitado) |
| `tags`          | List\<String\> | Tags para busca/filtro (ex: ["novidades", "promocao"]) |
| `attributes`    | JSON   | Atributos variáveis (cor, tamanho, material) – para v1 pode ser texto ou JSON simples |

---

## 1.1 Entidade `ProductImage` (fotos do produto)

Relação **1:N**: um produto possui várias fotos.

| Atributo   | Tipo    | Descrição                                      |
|------------|---------|------------------------------------------------|
| `id`       | UUID    | Chave primária                                 |
| `productId`| UUID    | FK para `product`                              |
| `url`      | String  | URL da imagem                                  |
| `position` | Integer | Ordem de exibição (1 = primeira, galeria)      |
| `isMain`   | Boolean | Se é a imagem principal (card/listagem)        |
| `altText`  | String  | Texto alternativo para acessibilidade/SEO      |

**Regras:**
- Um produto deve ter ao menos uma imagem principal (`isMain = true`).
- Ordenar por `position` para definir a sequência na galeria.
- Para listagem/card: usar a imagem com `isMain = true`; se inexistir, usar a de menor `position`.

---

## 2. Enums e Value Objects Sugeridos

### ProductStatus

```java
public enum ProductStatus {
    DRAFT,       // Rascunho, não visível no site
    ACTIVE,      // Ativo e visível
    INACTIVE,    // Desativado temporariamente
    OUT_OF_STOCK // Sem estoque (fornecedor)
}
```

---

## 3. Resumo dos Atributos Recomendados

| Grupo           | Atributos                                                                 |
|-----------------|---------------------------------------------------------------------------|
| **Identificação** | id, sku, slug, name, shortDescription, fullDescription                   |
| **Preços**      | costPrice, salePrice, compareAtPrice, currency                            |
| **Fornecedor**  | supplierSku, supplierName, supplierProductUrl, leadTimeDays, isDropship   |
| **Físico**      | weight, length, width, height                                             |
| **Status**      | status, stockQuantity                                                     |
| **Categorização** | categoryId, brand, tags, attributes                                     |
| **Mídia**       | `ProductImage` (tabela `product_images`: url, position, isMain, altText)  |
| **SEO**         | metaTitle, metaDescription                                                |
| **Auditoria**   | createdAt, updatedAt                                                      |

---

## 4. Considerações para Dropshipping

- **Estoque:** Em dropshipping clássico, o estoque fica no fornecedor. Pode-se usar `stockQuantity` para sinalizar indisponibilidade (0) ou manter como “ilimitado” e sincronizar via integração.
- **Imagens:** Tabela `product_images` com relação 1:N. Armazenar URLs (externas do fornecedor ou CDN). Campo `position` define ordem na galeria; `isMain` define a foto principal para cards/listagens.
- **Preços:** Manter `costPrice` e `salePrice` separados para cálculo de margem e relatórios.
- **Lead time:** Importante para exibir “envio em X dias úteis” no site.

---

## 5. Endpoints Sugeridos (CRUD)

- `POST /api/v1/products` – criar produto (ADMIN, MANAGER)
- `GET /api/v1/products` – listar com paginação e filtros (status, categoria, busca por nome/SKU)
- `GET /api/v1/products/{id}` – buscar por ID
- `GET /api/v1/products/slug/{slug}` – buscar por slug (para URLs amigáveis no site)
- `PUT /api/v1/products/{id}` – atualizar
- `PATCH /api/v1/products/{id}/status` – alterar status
- `DELETE /api/v1/products/{id}` – exclusão lógica ou física (definir política)

**Fotos do produto (sub-recurso):**

- `POST /api/v1/products/{id}/images` – adicionar foto (url, position, isMain, altText)
- `GET /api/v1/products/{id}/images` – listar fotos do produto (ordenadas por position)
- `PUT /api/v1/products/{id}/images/{imageId}` – atualizar foto (position, isMain, altText)
- `DELETE /api/v1/products/{id}/images/{imageId}` – remover foto

**Endpoint público (site):**

- `GET /api/v1/public/products` – listar apenas produtos com status `ACTIVE`, para exposição no site.

---

## 6. Próximos Passos

1. Criar migrations Flyway para tabelas `products`, `product_images` (e `categories` se houver)
2. Implementar domínio (Product, ProductStatus), portas e use cases
3. Implementar adapters JPA e controllers
4. Integrar com o frontend (listagem e detalhe de produtos no site)
