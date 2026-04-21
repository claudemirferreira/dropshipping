package com.srv.setebit.dropshipping.application.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ComercialRequest(
        @NotNull @DecimalMin(value = "0", inclusive = false)
        @JsonProperty("valor_custo") BigDecimal valorCusto,
        @NotNull @DecimalMin(value = "0", inclusive = false)
        @JsonProperty("valor_venda") BigDecimal valorVenda,
        @DecimalMin(value = "0", inclusive = false)
        @JsonProperty("percentual_taxa_seller") BigDecimal percentualTaxaSeller,
        @NotBlank
        @JsonProperty("garantia") String garantia
) {}

