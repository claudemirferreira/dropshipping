package com.srv.setebit.dropshipping.application.access.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Dados da rotina")
public record RotinaResponse(
        @Schema(description = "ID da rotina")
        UUID id,

        @Schema(description = "Código único")
        String code,

        @Schema(description = "Nome")
        String name,

        @Schema(description = "Descrição")
        String description,

        @Schema(description = "Ícone")
        String icon,

        @Schema(description = "Rota da página")
        String path,

        @Schema(description = "Ativa")
        boolean active,

        @Schema(description = "Ordem de exibição no menu")
        int displayOrder,

        @Schema(description = "Data de criação")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {
}
