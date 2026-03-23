package com.srv.setebit.dropshipping.application.product.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srv.setebit.dropshipping.domain.product.ProductStatus;

import java.time.Instant;
import java.util.UUID;

public record ProductResponseDTO(
        @JsonProperty("id") UUID id,
        @JsonProperty("nome") String nome,
        @JsonProperty("slug") String slug,
        @JsonProperty("sku") String sku,
        @JsonProperty("status") ProductStatus status,
        @JsonProperty("ean") String ean,
        @JsonProperty("is_ean_interno") boolean isEanInterno,
        @JsonProperty("criado_em") Instant criadoEm
) {}

