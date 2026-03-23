package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.product.CreateBaseProductUseCase;
import com.srv.setebit.dropshipping.application.product.dto.request.ProductRequestDTO;
import com.srv.setebit.dropshipping.application.product.dto.response.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/admin/products")
@Tag(name = "Produtos - Admin", description = "Administração de produtos base do catálogo")
public class ProductAdminController {

    private final CreateBaseProductUseCase createBaseProductUseCase;

    public ProductAdminController(CreateBaseProductUseCase createBaseProductUseCase) {
        this.createBaseProductUseCase = createBaseProductUseCase;
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
}

