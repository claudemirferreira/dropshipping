package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.product.*;
import com.srv.setebit.dropshipping.application.product.dto.request.*;
import com.srv.setebit.dropshipping.application.product.dto.response.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Produtos", description = "CRUD de produtos (ADMIN/MANAGER)")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetProductBySlugUseCase getProductBySlugUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateProductStatusUseCase updateProductStatusUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final AddProductImageUseCase addProductImageUseCase;
    private final ListProductImagesUseCase listProductImagesUseCase;
    private final UpdateProductImageUseCase updateProductImageUseCase;
    private final RemoveProductImageUseCase removeProductImageUseCase;

    public ProductController(CreateProductUseCase createProductUseCase,
                             GetProductByIdUseCase getProductByIdUseCase,
                             GetProductBySlugUseCase getProductBySlugUseCase,
                             ListProductsUseCase listProductsUseCase,
                             UpdateProductUseCase updateProductUseCase,
                             UpdateProductStatusUseCase updateProductStatusUseCase,
                             DeleteProductUseCase deleteProductUseCase,
                             AddProductImageUseCase addProductImageUseCase,
                             ListProductImagesUseCase listProductImagesUseCase,
                             UpdateProductImageUseCase updateProductImageUseCase,
                             RemoveProductImageUseCase removeProductImageUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getProductBySlugUseCase = getProductBySlugUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.updateProductStatusUseCase = updateProductStatusUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.addProductImageUseCase = addProductImageUseCase;
        this.listProductImagesUseCase = listProductImagesUseCase;
        this.updateProductImageUseCase = updateProductImageUseCase;
        this.removeProductImageUseCase = removeProductImageUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Criar produto")
    public ResponseEntity<ProductDetailResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductDetailResponse response = createProductUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar produtos")
    public ResponseEntity<PageProductResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID categoryId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PageProductResponse response = listProductsUseCase.execute(name, status, categoryId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Buscar produto por ID")
    public ResponseEntity<ProductDetailResponse> getById(@PathVariable UUID id) {
        ProductDetailResponse response = getProductByIdUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Buscar produto por slug")
    public ResponseEntity<ProductDetailResponse> getBySlug(@PathVariable String slug) {
        ProductDetailResponse response = getProductBySlugUseCase.execute(slug);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Atualizar produto")
    public ResponseEntity<ProductDetailResponse> update(@PathVariable UUID id,
                                                        @Valid @RequestBody UpdateProductRequest request) {
        ProductDetailResponse response = updateProductUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Alterar status do produto")
    public ResponseEntity<ProductDetailResponse> updateStatus(@PathVariable UUID id,
                                                              @Valid @RequestBody UpdateProductStatusRequest request) {
        ProductDetailResponse response = updateProductStatusUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Excluir produto")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteProductUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Adicionar imagem ao produto")
    public ResponseEntity<ProductImageResponse> addImage(@PathVariable UUID id,
                                                         @Valid @RequestBody CreateProductImageRequest request) {
        ProductImageResponse response = addProductImageUseCase.execute(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar imagens do produto")
    public ResponseEntity<List<ProductImageResponse>> listImages(@PathVariable UUID id) {
        List<ProductImageResponse> response = listProductImagesUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Atualizar imagem do produto")
    public ResponseEntity<ProductImageResponse> updateImage(@PathVariable UUID id,
                                                            @PathVariable UUID imageId,
                                                            @Valid @RequestBody UpdateProductImageRequest request) {
        ProductImageResponse response = updateProductImageUseCase.execute(id, imageId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/images/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Remover imagem do produto")
    public ResponseEntity<Void> removeImage(@PathVariable UUID id, @PathVariable UUID imageId) {
        removeProductImageUseCase.execute(id, imageId);
        return ResponseEntity.noContent().build();
    }
}
