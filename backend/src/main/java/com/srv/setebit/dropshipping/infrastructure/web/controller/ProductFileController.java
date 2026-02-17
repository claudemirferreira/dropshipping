package com.srv.setebit.dropshipping.infrastructure.web.controller;

import com.srv.setebit.dropshipping.application.product.dto.response.ProductFileResponse;
import com.srv.setebit.dropshipping.domain.product.ProductFile;
import com.srv.setebit.dropshipping.domain.product.ProductFileType;
import com.srv.setebit.dropshipping.domain.product.exception.ProductNotFoundException;
import com.srv.setebit.dropshipping.domain.product.port.ProductFileRepositoryPort;
import com.srv.setebit.dropshipping.domain.product.port.ProductRepositoryPort;
import com.srv.setebit.dropshipping.infrastructure.storage.ProductFileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Arquivos de Produto", description = "Upload de fotos e vídeos de produtos")
public class ProductFileController {

    private final ProductRepositoryPort productRepository;
    private final ProductFileRepositoryPort productFileRepository;
    private final ProductFileStorageService storageService;

    public ProductFileController(ProductRepositoryPort productRepository,
                                 ProductFileRepositoryPort productFileRepository,
                                 ProductFileStorageService storageService) {
        this.productRepository = productRepository;
        this.productFileRepository = productFileRepository;
        this.storageService = storageService;
    }

    @PostMapping(
            value = "/{id}/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Upload de foto/vídeo de produto")
    public ResponseEntity<ProductFileResponse> uploadFile(@PathVariable UUID id,
                                                          @RequestParam("file") MultipartFile file,
                                                          @RequestParam("type") @NotNull String type,
                                                          @RequestParam(value = "isMain", required = false, defaultValue = "false") boolean isMain,
                                                          @RequestParam(value = "position", required = false) Integer position,
                                                          @RequestParam(value = "altText", required = false) String altText) throws IOException {
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        ProductFileType fileType = ProductFileType.valueOf(type.toUpperCase());
        ProductFile saved = storageService.upload(id, file, fileType, isMain, position, altText);

        ProductFileResponse response = new ProductFileResponse(
                saved.getId(),
                saved.getFileType(),
                saved.getObjectName(),
                saved.getOriginalName(),
                saved.getPosition(),
                saved.isMain(),
                saved.getAltText()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/files")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar arquivos (fotos/vídeos) de um produto")
    public ResponseEntity<List<ProductFileResponse>> listFiles(@PathVariable UUID id) {
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

        List<ProductFileResponse> files = productFileRepository.findByProductId(id).stream()
                .map(f -> new ProductFileResponse(
                        f.getId(),
                        f.getFileType(),
                        f.getObjectName(),
                        f.getOriginalName(),
                        f.getPosition(),
                        f.isMain(),
                        f.getAltText()
                ))
                .toList();
        return ResponseEntity.ok(files);
    }
}

