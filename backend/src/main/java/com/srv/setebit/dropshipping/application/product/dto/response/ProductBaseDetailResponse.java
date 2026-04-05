package com.srv.setebit.dropshipping.application.product.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srv.setebit.dropshipping.domain.product.Product;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados completos do produto no formato base (aninhado)")
public record ProductBaseDetailResponse(
        @JsonProperty("nome")
        @Schema(description = "Nome do produto")
        String nome,

        @JsonProperty("slug")
        @Schema(description = "Slug do produto")
        String slug,

        @JsonProperty("sku")
        @Schema(description = "SKU do produto")
        String sku,

        @JsonProperty("categoria_id")
        @Schema(description = "ID da categoria")
        String categoriaId,

        @JsonProperty("marca")
        @Schema(description = "Marca do produto")
        String marca,

        @JsonProperty("descricao_curta")
        @Schema(description = "Descrição curta")
        String descricaoCurta,

        @JsonProperty("descricao_completa")
        @Schema(description = "Descrição completa")
        String descricaoCompleta,

        @JsonProperty("logistica")
        LogisticaDetail logistica,

        @JsonProperty("estoque")
        EstoqueDetail estoque,

        @JsonProperty("comercial")
        ComercialDetail comercial,

        @JsonProperty("codigos")
        CodigosDetail codigos,

        @JsonProperty("tags")
        @Schema(description = "Tags do produto")
        List<String> tags
) {
    public static ProductBaseDetailResponse fromEntity(Product p) {
        return new ProductBaseDetailResponse(
                p.getName(),
                p.getSlug(),
                p.getSku(),
                p.getCategoryId() != null ? p.getCategoryId().toString() : null,
                p.getBrand(),
                p.getShortDescription(),
                p.getFullDescription(),
                new LogisticaDetail(
                        p.getWeight(),
                        p.getHeight(),
                        p.getWidth(),
                        p.getLength(),
                        p.getLeadTimeDays()
                ),
                new EstoqueDetail(
                        p.getStockQuantity() != null ? p.getStockQuantity() : 0,
                        p.getStockMinimum() != null ? p.getStockMinimum() : 0
                ),
                new ComercialDetail(
                        p.getCostPrice(),
                        p.getSellerFeePercent(),
                        p.getWarranty() != null ? p.getWarranty() : ""
                ),
                new CodigosDetail(
                        p.getEan(),
                        p.isEanInterno()
                ),
                p.getTags() != null ? List.of(p.getTags().split(",")) : List.of()
        );
    }

    public record LogisticaDetail(
            @JsonProperty("peso_kg") BigDecimal pesoKg,
            @JsonProperty("altura_cm") BigDecimal alturaCm,
            @JsonProperty("largura_cm") BigDecimal larguraCm,
            @JsonProperty("comprimento_cm") BigDecimal comprimentoCm,
            @JsonProperty("lead_time_envio_dias") Integer leadTimeEnvioDias
    ) {}

    public record EstoqueDetail(
            @JsonProperty("atual") Integer atual,
            @JsonProperty("minimo") Integer minimo
    ) {}

    public record ComercialDetail(
            @JsonProperty("valor_custo") BigDecimal valorCusto,
            @JsonProperty("percentual_taxa_seller") BigDecimal percentualTaxaSeller,
            @JsonProperty("garantia") String garantia
    ) {}

    public record CodigosDetail(
            @JsonProperty("ean") String ean,
            @JsonProperty("is_ean_interno") Boolean isEanInterno
    ) {}
}
