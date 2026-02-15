package com.srv.setebit.dropshipping.application.product.dto.response;

import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados do produto")
public record ProductResponse(
        @Schema(description = "ID do produto")
        UUID id,

        @Schema(description = "SKU do produto")
        String sku,

        @Schema(description = "Nome do produto")
        String name,

        @Schema(description = "Descrição curta")
        String shortDescription,

        @Schema(description = "Preço de venda")
        BigDecimal salePrice,

        @Schema(description = "Preço de custo")
        BigDecimal costPrice,

        @Schema(description = "Moeda")
        String currency,

        @Schema(description = "Status do produto")
        ProductStatus status,

        @Schema(description = "Slug para URL")
        String slug,

        @Schema(description = "URL da imagem principal")
        String mainImageUrl,

        @Schema(description = "Data de criação")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {
}
