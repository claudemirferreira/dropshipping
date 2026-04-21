package com.srv.setebit.dropshipping.application.appconfig.dto.response;

import com.srv.setebit.dropshipping.domain.appconfig.enums.TipoConfigEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Configuração (tipo + payload JSON)")
public record AppConfigResponse(
        @Schema(description = "ID")
        UUID id,

        @Schema(description = "Tipo (código persistido)")
        TipoConfigEnum tipo,

        @Schema(description = "Payload JSON")
        Map<String, Object> payload,

        @Schema(description = "Data de criação")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {
}
