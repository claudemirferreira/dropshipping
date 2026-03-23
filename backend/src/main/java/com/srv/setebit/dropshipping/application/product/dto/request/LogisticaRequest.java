package com.srv.setebit.dropshipping.application.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LogisticaRequest(
        @NotNull @DecimalMin(value = "0", inclusive = false)
        @JsonProperty("peso_kg") BigDecimal pesoKg,
        @NotNull @DecimalMin(value = "0", inclusive = false)
        @JsonProperty("altura_cm") BigDecimal alturaCm,
        @NotNull @DecimalMin(value = "0", inclusive = false)
        @JsonProperty("largura_cm") BigDecimal larguraCm,
        @NotNull @DecimalMin(value = "0", inclusive = false)
        @JsonProperty("comprimento_cm") BigDecimal comprimentoCm,
        @NotNull @Min(1)
        @JsonProperty("lead_time_envio_dias") Integer leadTimeEnvioDias
) {}

