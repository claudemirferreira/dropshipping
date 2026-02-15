# Tasks: Manter Produto (CRUD + Fotos)

> Documento de tarefas detalhadas para implementação do módulo de produtos conforme `prompt-produtos-dropshipping.md`.

---

## Fase 1: Banco de Dados (Flyway)

### Task 1.1 – Migration: tabela `products`

- [ ] Criar `V4__create_products_table.sql`
- [ ] Colunas: id, sku, name, short_description, full_description, sale_price, cost_price, currency, status
- [ ] Colunas opcionais: supplier_sku, supplier_name, supplier_product_url, lead_time_days, is_dropship
- [ ] Colunas físicas: weight, length, width, height
- [ ] Colunas categorização: slug, category_id, brand, meta_title, meta_description
- [ ] Colunas opcionais: compare_at_price, stock_quantity, tags (JSONB ou TEXT), attributes (JSONB)
- [ ] Constraint `uk_products_sku` UNIQUE (sku)
- [ ] Constraint `uk_products_slug` UNIQUE (slug)
- [ ] Índices: products(sku), products(slug), products(status), products(category_id)
- [ ] Compatível com PostgreSQL e H2 (usar tipos genéricos, evitar funções específicas)

### Task 1.2 – Migration: tabela `product_images`

- [ ] Criar `V5__create_product_images_table.sql`
- [ ] Colunas: id, product_id (FK products), url, position, is_main, alt_text
- [ ] Constraint `fk_product_images_product` ON DELETE CASCADE
- [ ] Índice: product_images(product_id)

---

## Fase 2: Domínio

### Task 2.1 – Entidades e enums de domínio

- [ ] Criar `domain/product/ProductStatus.java` (enum: DRAFT, ACTIVE, INACTIVE, OUT_OF_STOCK)
- [ ] Criar `domain/product/Product.java` (entidade de domínio, sem JPA)
- [ ] Criar `domain/product/ProductImage.java` (entidade de domínio)
- [ ] Product: atributos conforme prompt; List<ProductImage> images (ou acessar via repositório)

### Task 2.2 – Portas (interfaces de repositório)

- [ ] Criar `domain/product/port/ProductRepositoryPort.java`
  - [ ] save(Product)
  - [ ] findById(UUID)
  - [ ] findBySku(String)
  - [ ] findBySlug(String)
  - [ ] existsBySku(String)
  - [ ] existsBySlug(String)
  - [ ] findAllByFilter(name, status, categoryId, pageable) → Page<Product>
- [ ] Criar `domain/product/port/ProductImageRepositoryPort.java`
  - [ ] save(ProductImage)
  - [ ] findByProductId(UUID) → List<ProductImage> ordenado por position
  - [ ] findById(UUID)
  - [ ] deleteById(UUID)
  - [ ] deleteByProductId(UUID)

### Task 2.3 – Exceções de domínio

- [ ] Criar `domain/product/exception/ProductNotFoundException.java`
- [ ] Criar `domain/product/exception/DuplicateSkuException.java`
- [ ] Criar `domain/product/exception/DuplicateSlugException.java`
- [ ] Criar `domain/product/exception/ProductImageNotFoundException.java`

---

## Fase 3: Application (Use Cases e DTOs)

### Task 3.1 – DTOs de request

- [ ] Criar `application/product/dto/request/CreateProductRequest.java`
- [ ] Criar `application/product/dto/request/UpdateProductRequest.java`
- [ ] Criar `application/product/dto/request/CreateProductImageRequest.java` (url, position, isMain, altText)
- [ ] Criar `application/product/dto/request/UpdateProductImageRequest.java`
- [ ] Criar `application/product/dto/request/UpdateProductStatusRequest.java` (status)
- [ ] Bean Validation em todos (NotNull, Size, Min, etc.)

### Task 3.2 – DTOs de response

- [ ] Criar `application/product/dto/response/ProductResponse.java` (com campos principais)
- [ ] Criar `application/product/dto/response/ProductDetailResponse.java` (inclui List<ProductImageResponse>)
- [ ] Criar `application/product/dto/response/ProductImageResponse.java`
- [ ] Criar `application/product/dto/response/PageProductResponse.java` (content, totalElements, totalPages, etc.)

### Task 3.3 – Use Cases: CRUD produto

- [ ] Criar `CreateProductUseCase`
  - [ ] Validar SKU único
  - [ ] Gerar slug (a partir do name ou recebido) e validar único
  - [ ] Persistir produto
- [ ] Criar `GetProductByIdUseCase`
- [ ] Criar `GetProductBySlugUseCase`
- [ ] Criar `ListProductsUseCase` (filtros: name, status, categoryId; paginação)
- [ ] Criar `UpdateProductUseCase`
  - [ ] Validar slug único se alterado
- [ ] Criar `UpdateProductStatusUseCase`
- [ ] Criar `DeleteProductUseCase` (exclusão física ou lógica – definir política)

### Task 3.4 – Use Cases: fotos do produto

- [ ] Criar `AddProductImageUseCase`
  - [ ] Validar produto existe
  - [ ] Se isMain=true, desmarcar outras como main
  - [ ] Persistir ProductImage
- [ ] Criar `ListProductImagesUseCase` (ordenado por position)
- [ ] Criar `UpdateProductImageUseCase` (position, isMain, altText)
- [ ] Criar `RemoveProductImageUseCase`
  - [ ] Garantir ao menos uma imagem main se remover a atual main (ou validar antes)

---

## Fase 4: Infrastructure (Persistence)

### Task 4.1 – Entidades JPA

- [ ] Criar `infrastructure/persistence/jpa/ProductEntity.java`
- [ ] Criar `infrastructure/persistence/jpa/ProductImageEntity.java`
- [ ] Relacionamento @OneToMany Product -> ProductImage (cascade ALL, orphanRemoval)
- [ ] Mapeamento de ProductStatus como @Enumerated(STRING)
- [ ] Mapeamento de tags/attributes como @JdbcTypeCode(SqlTypes.JSON) ou equivalente

### Task 4.2 – Repositories JPA

- [ ] Criar `ProductJpaRepository` extends JpaRepository
  - [ ] Métodos custom: existsBySku, existsBySlugExcludingId
  - [ ] @Query para findAllByFilter (name ILIKE, status, categoryId)
- [ ] Criar `ProductImageJpaRepository`

### Task 4.3 – Adapters

- [ ] Criar `ProductRepositoryAdapter` implements ProductRepositoryPort
- [ ] Criar `ProductImageRepositoryAdapter` implements ProductImageRepositoryPort
- [ ] Mapeamento Entity <-> Domain nos adapters

---

## Fase 5: Infrastructure (Web – Controllers)

### Task 5.1 – ProductController (admin)

- [ ] Criar `ProductController` em `/api/v1/products`
- [ ] POST / – CreateProductUseCase (@PreAuthorize ADMIN, MANAGER)
- [ ] GET / – ListProductsUseCase (filtros: name, status, categoryId, pageable)
- [ ] GET /{id} – GetProductByIdUseCase
- [ ] GET /slug/{slug} – GetProductBySlugUseCase
- [ ] PUT /{id} – UpdateProductUseCase
- [ ] PATCH /{id}/status – UpdateProductStatusUseCase
- [ ] DELETE /{id} – DeleteProductUseCase
- [ ] Documentar com @Operation e @Schema (Swagger)

### Task 5.2 – ProductImageController (sub-recurso)

- [ ] POST /{id}/images – AddProductImageUseCase
- [ ] GET /{id}/images – ListProductImagesUseCase
- [ ] PUT /{id}/images/{imageId} – UpdateProductImageUseCase
- [ ] DELETE /{id}/images/{imageId} – RemoveProductImageUseCase
- [ ] Ou incluir endpoints no mesmo ProductController

### Task 5.3 – ProductPublicController (site)

- [ ] Criar controller em `/api/v1/public/products`
- [ ] GET / – ListProductsUseCase filtrando apenas status=ACTIVE
- [ ] GET /{id} ou GET /slug/{slug} – GetProductDetail (com imagens)
- [ ] Endpoints públicos: permitir sem autenticação no SecurityConfig

### Task 5.4 – Tratamento de erros

- [ ] Registrar ProductNotFoundException -> 404 no GlobalExceptionHandler
- [ ] Registrar DuplicateSkuException, DuplicateSlugException -> 409 Conflict
- [ ] Registrar ProductImageNotFoundException -> 404

---

## Fase 6: Segurança e Configuração

### Task 6.1 – SecurityConfig

- [ ] Adicionar `.requestMatchers("/api/v1/products/**").authenticated()`
- [ ] Adicionar `.requestMatchers("/api/v1/public/products/**").permitAll()`

---

## Fase 7: Testes

### Task 7.1 – Testes unitários (Use Cases)

- [ ] CreateProductUseCaseTest (sucesso, sku duplicado, slug duplicado)
- [ ] UpdateProductUseCaseTest
- [ ] AddProductImageUseCaseTest (sucesso, isMain desmarca outras)
- [ ] RemoveProductImageUseCaseTest

### Task 7.2 – Testes de integração

- [ ] ProductControllerIntegrationTest (CRUD completo)
- [ ] ProductPublicControllerIntegrationTest (listar e buscar sem auth)
- [ ] ProductImageControllerIntegrationTest
- [ ] Massa de dados: inserir produtos de teste em test-data.sql ou migration de teste

### Task 7.3 – Validação Flyway

- [ ] Garantir que testes rodam com H2 e aplicam V4 e V5
- [ ] Validar compatibilidade SQL (PostgreSQL + H2)

---

## Fase 8: Documentação e Dados de desenvolvimento

### Task 8.1 – Swagger

- [ ] Schemas dos DTOs documentados
- [ ] Exemplos de request/response nos endpoints

### Task 8.2 – DevDataLoader (opcional)

- [ ] Inserir 2–3 produtos de exemplo no perfil `dev` para facilitar testes manuais

---

## Ordem sugerida de execução

1. **1.1, 1.2** – Migrations
2. **2.1, 2.2, 2.3** – Domínio
3. **3.1, 3.2** – DTOs
4. **3.3, 3.4** – Use Cases
5. **4.1, 4.2, 4.3** – Persistence
6. **5.1, 5.2, 5.3, 5.4** – Controllers e erros
7. **6.1** – SecurityConfig
8. **7.x** – Testes
9. **8.x** – Documentação e dados dev

---

## Checklist rápido

| Fase | Tasks | Status |
|------|-------|--------|
| 1. Banco | 1.1, 1.2 | ☐ |
| 2. Domínio | 2.1, 2.2, 2.3 | ☐ |
| 3. Application | 3.1, 3.2, 3.3, 3.4 | ☐ |
| 4. Persistence | 4.1, 4.2, 4.3 | ☐ |
| 5. Web | 5.1, 5.2, 5.3, 5.4 | ☐ |
| 6. Security | 6.1 | ☐ |
| 7. Testes | 7.1, 7.2, 7.3 | ☐ |
| 8. Docs/Dev | 8.1, 8.2 | ☐ |
