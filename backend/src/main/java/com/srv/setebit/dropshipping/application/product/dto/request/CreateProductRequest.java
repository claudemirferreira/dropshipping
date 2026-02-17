package com.srv.setebit.dropshipping.application.product.dto.request;

import com.srv.setebit.dropshipping.domain.product.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "Dados para criação de produto")
public record CreateProductRequest(
        @NotBlank(message = "SKU é obrigatório")
        @Length(max = 100)
        @Schema(description = "Código SKU do produto", example = "PROD-001", required = true)
        String sku,

        @NotBlank(message = "Nome é obrigatório")
        @Length(max = 255)
        @Schema(description = "Nome do produto", example = "Camiseta Básica", required = true)
        String name,

        @NotBlank(message = "Descrição curta é obrigatória")
        @Length(max = 500)
        @Schema(description = "Descrição curta para listagens", required = true)
        String shortDescription,

        @Schema(description = "Descrição completa do produto")
        String fullDescription,

        @NotNull(message = "Preço de venda é obrigatório")
        @DecimalMin(value = "0", inclusive = false, message = "Preço de venda deve ser positivo")
        @Schema(description = "Preço de venda", required = true)
        BigDecimal salePrice,

        @NotNull(message = "Preço de custo é obrigatório")
        @DecimalMin(value = "0", message = "Preço de custo não pode ser negativo")
        @Schema(description = "Preço de custo (fornecedor)", required = true)
        BigDecimal costPrice,

        @NotBlank(message = "Moeda é obrigatória")
        @Length(max = 10)
        @Schema(description = "Moeda", example = "BRL", required = true)
        String currency,

        @NotNull(message = "Status é obrigatório")
        @Schema(description = "Status do produto", required = true)
        ProductStatus status,

        @Length(max = 100)
        @Schema(description = "SKU do fornecedor")
        String supplierSku,

        @Length(max = 255)
        @Schema(description = "Nome do fornecedor")
        String supplierName,

        @Length(max = 1000)
        @Schema(description = "URL do produto no site do fornecedor")
        String supplierProductUrl,

        @Min(0)
        @Schema(description = "Prazo de envio em dias")
        Integer leadTimeDays,

        @Schema(description = "É produto dropship", example = "true")
        Boolean isDropship,

        @DecimalMin("0")
        @Schema(description = "Peso em kg")
        BigDecimal weight,

        @DecimalMin("0")
        @Schema(description = "Comprimento em cm")
        BigDecimal length,

        @DecimalMin("0")
        @Schema(description = "Largura em cm")
        BigDecimal width,

        @DecimalMin("0")
        @Schema(description = "Altura em cm")
        BigDecimal height,

        @NotBlank(message = "Slug é obrigatório")
        @Length(max = 255)
        @Schema(description = "URL amigável", example = "camiseta-basica", required = true)
        String slug,

        @Schema(description = "ID da categoria")
        UUID categoryId,

        @Length(max = 255)
        @Schema(description = "Marca")
        String brand,

        @Length(max = 255)
        @Schema(description = "Título para SEO")
        String metaTitle,

        @Length(max = 500)
        @Schema(description = "Descrição para meta tags SEO")
        String metaDescription,

        @DecimalMin("0")
        @Schema(description = "Preço de comparação (de)")
        BigDecimal compareAtPrice,

        @Min(0)
        @Schema(description = "Quantidade em estoque")
        Integer stockQuantity

) {
}
