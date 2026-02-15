package com.srv.setebit.dropshipping.application.access.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.UUID;

@Schema(description = "IDs dos perfis a atribuir ao usuário")
public record AssignPerfisRequest(
        @Schema(description = "IDs dos perfis")
        Set<UUID> perfilIds
) {
}
