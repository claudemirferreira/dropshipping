package com.srv.setebit.dropshipping.application.access.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Schema(description = "Dados do perfil")
public record PerfilResponse(
        @Schema(description = "ID do perfil")
        UUID id,

        @Schema(description = "Código único")
        String code,

        @Schema(description = "Nome")
        String name,

        @Schema(description = "Descrição")
        String description,

        @Schema(description = "Ícone")
        String icon,

        @Schema(description = "Ativo")
        boolean active,

        @Schema(description = "Ordem de exibição no menu")
        int displayOrder,

        @Schema(description = "Rotinas do perfil")
        Set<RotinaResponse> rotinas,

        @Schema(description = "Data de criação")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {
}
