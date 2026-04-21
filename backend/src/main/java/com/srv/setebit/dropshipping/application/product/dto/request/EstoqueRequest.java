package com.srv.setebit.dropshipping.application.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EstoqueRequest(
        @NotNull @Min(0) @JsonProperty("atual") Integer atual,
        @NotNull @Min(0) @JsonProperty("minimo") Integer minimo
) {}

