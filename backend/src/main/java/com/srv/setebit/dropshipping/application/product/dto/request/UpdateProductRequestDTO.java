package com.srv.setebit.dropshipping.application.product.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record UpdateProductRequestDTO(
        @NotBlank
        @Length(max = 60)
        @JsonProperty("nome")
        String nome,

        @JsonProperty("slug")
        String slug,

        @NotBlank
        @JsonProperty("sku")
        String sku,

        @JsonProperty("categoria_id")
        String categoriaId,

        @NotBlank
        @JsonProperty("marca")
        String marca,

        @NotBlank
        @Length(max = 255)
        @JsonProperty("descricao_curta")
        String descricaoCurta,

        @NotBlank
        @JsonProperty("descricao_completa")
        String descricaoCompleta,

        @Valid
        @JsonProperty("logistica")
        LogisticaRequest logistica,

        @Valid
        @JsonProperty("estoque")
        EstoqueRequest estoque,

        @Valid
        @JsonProperty("comercial")
        ComercialRequest comercial,

        @Valid
        @JsonProperty("codigos")
        CodigosRequest codigos,

        @JsonProperty("tags")
        List<String> tags
)
{}
