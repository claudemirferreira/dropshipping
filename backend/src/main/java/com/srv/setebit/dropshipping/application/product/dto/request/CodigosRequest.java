package com.srv.setebit.dropshipping.application.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public record CodigosRequest(
        @Pattern(regexp = "^\\d{8,14}$", message = "EAN deve conter apenas dígitos (8-14)")
        @JsonProperty("ean") String ean,
        @JsonProperty("is_ean_interno") Boolean isEanInterno
) {}

