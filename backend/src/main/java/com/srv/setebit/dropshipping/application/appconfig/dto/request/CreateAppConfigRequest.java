package com.srv.setebit.dropshipping.application.appconfig.dto.request;

import com.srv.setebit.dropshipping.domain.appconfig.enums.TipoConfigEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(description = "Dados para criação de config")
public record CreateAppConfigRequest(
        @NotNull(message = "Tipo é obrigatório")
        @Schema(description = "Tipo da configuração (código)", example = "mercado_livre", requiredMode = Schema.RequiredMode.REQUIRED)
        TipoConfigEnum tipo,

        @NotNull(message = "Payload é obrigatório")
        @Schema(description = "Corpo JSON (objeto)", requiredMode = Schema.RequiredMode.REQUIRED)
        Map<String, Object> payload
) {
}
