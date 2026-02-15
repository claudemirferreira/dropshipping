package com.srv.setebit.dropshipping.application.product.dto.response;

import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados completos do produto (inclui imagens)")
public record ProductDetailResponse(
        @Schema(description = "ID do produto")
        UUID id,

        @Schema(description = "SKU do produto")
        String sku,

        @Schema(description = "Nome do produto")
        String name,

        @Schema(description = "Descrição curta")
        String shortDescription,

        @Schema(description = "Descrição completa")
        String fullDescription,

        @Schema(description = "Preço de venda")
        BigDecimal salePrice,

        @Schema(description = "Preço de custo")
        BigDecimal costPrice,

        @Schema(description = "Moeda")
        String currency,

        @Schema(description = "Status do produto")
        ProductStatus status,

        @Schema(description = "SKU do fornecedor")
        String supplierSku,

        @Schema(description = "Nome do fornecedor")
        String supplierName,

        @Schema(description = "URL do produto no fornecedor")
        String supplierProductUrl,

        @Schema(description = "Prazo de envio em dias")
        Integer leadTimeDays,

        @Schema(description = "É produto dropship")
        boolean isDropship,

        @Schema(description = "Peso em kg")
        BigDecimal weight,

        @Schema(description = "Comprimento em cm")
        BigDecimal length,

        @Schema(description = "Largura em cm")
        BigDecimal width,

        @Schema(description = "Altura em cm")
        BigDecimal height,

        @Schema(description = "Slug para URL")
        String slug,

        @Schema(description = "ID da categoria")
        UUID categoryId,

        @Schema(description = "Marca")
        String brand,

        @Schema(description = "Título SEO")
        String metaTitle,

        @Schema(description = "Descrição SEO")
        String metaDescription,

        @Schema(description = "Preço de comparação")
        BigDecimal compareAtPrice,

        @Schema(description = "Quantidade em estoque")
        Integer stockQuantity,

        @Schema(description = "Imagens do produto")
        List<ProductImageResponse> images,

        @Schema(description = "Data de criação")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {
}
