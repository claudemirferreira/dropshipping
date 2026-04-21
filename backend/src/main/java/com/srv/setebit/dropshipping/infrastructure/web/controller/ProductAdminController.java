package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.product.CreateBaseProductUseCase;
import com.srv.setebit.dropshipping.application.product.UpdateBaseProductUseCase;
import com.srv.setebit.dropshipping.application.product.dto.request.ProductRequestDTO;
import com.srv.setebit.dropshipping.application.product.dto.request.UpdateProductRequestDTO;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductBaseDetailResponse;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductResponseDTO;
import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/admin/products")
@Tag(name = "Produtos - Admin", description = "Administração de produtos base do catálogo")
public class ProductAdminController {

    private final CreateBaseProductUseCase createBaseProductUseCase;
    private final UpdateBaseProductUseCase updateBaseProductUseCase;
    private final ProductRepositoryPort productRepository;

    public ProductAdminController(CreateBaseProductUseCase createBaseProductUseCase,
                                  UpdateBaseProductUseCase updateBaseProductUseCase,
                                  ProductRepositoryPort productRepository) {
        this.createBaseProductUseCase = createBaseProductUseCase;
        this.updateBaseProductUseCase = updateBaseProductUseCase;
        this.productRepository = productRepository;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Criar produto base")
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO request,
                                                     Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        ProductResponseDTO response = createBaseProductUseCase.execute(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Atualizar produto base")
    public ResponseEntity<ProductResponseDTO> update(@PathVariable UUID id,
                                                     @Valid @RequestBody UpdateProductRequestDTO request) {
        ProductResponseDTO response = updateBaseProductUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/detail")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Obter detalhes do produto base (formato aninhado)")
    public ResponseEntity<ProductBaseDetailResponse> getDetail(@PathVariable UUID id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return ResponseEntity.ok(ProductBaseDetailResponse.fromEntity(product));
    }
}

