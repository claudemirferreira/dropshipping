package com.srv.setebit.dropshipping.application.product.dto.request;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequestDTO(
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
        @NotNull
        @JsonProperty("logistica")
        LogisticaRequest logistica,

        @Valid
        @NotNull
        @JsonProperty("estoque")
        EstoqueRequest estoque,

        @Valid
        @NotNull
        @JsonProperty("comercial")
        ComercialRequest comercial,

        @Valid
        @JsonProperty("codigos")
        CodigosRequest codigos,

        @JsonProperty("tags")
        List<String> tags
)
{}
