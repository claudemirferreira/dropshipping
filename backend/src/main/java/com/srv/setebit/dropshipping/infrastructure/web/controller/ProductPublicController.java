package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.product.GetProductByIdUseCase;
import com.srv.setebit.dropshipping.application.product.GetProductBySlugUseCase;
import com.srv.setebit.dropshipping.application.product.ListProductsUseCase;
import com.srv.setebit.dropshipping.application.product.dto.response.PageProductResponse;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductDetailResponse;
import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/public/products")
@Tag(name = "Produtos (Público)", description = "Listagem e detalhe de produtos para o site")
public class ProductPublicController {

    private final ListProductsUseCase listProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetProductBySlugUseCase getProductBySlugUseCase;

    public ProductPublicController(ListProductsUseCase listProductsUseCase,
                                   GetProductByIdUseCase getProductByIdUseCase,
                                   GetProductBySlugUseCase getProductBySlugUseCase) {
        this.listProductsUseCase = listProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getProductBySlugUseCase = getProductBySlugUseCase;
    }

    @GetMapping
    @Operation(summary = "Listar produtos ativos (público)")
    public ResponseEntity<PageProductResponse> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        PageProductResponse response = listProductsUseCase.execute(name, ProductStatus.ACTIVE.name(), categoryId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID (público)")
    public ResponseEntity<ProductDetailResponse> getById(@PathVariable UUID id) {
        ProductDetailResponse response = getProductByIdUseCase.execute(id);
        if (response.status() != ProductStatus.ACTIVE) {
            throw new com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException(id);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Buscar produto por slug (público)")
    public ResponseEntity<ProductDetailResponse> getBySlug(@PathVariable String slug) {
        ProductDetailResponse response = getProductBySlugUseCase.execute(slug);
        if (response.status() != ProductStatus.ACTIVE) {
            throw new com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException(slug);
        }
        return ResponseEntity.ok(response);
    }
}
