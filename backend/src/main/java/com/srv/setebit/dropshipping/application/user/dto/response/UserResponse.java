package com.srv.setebit.dropshipping.application.user.dto.response;

import com.srv.setebit.dropshipping.domain.user.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Dados do usuário")
public record UserResponse(
        @Schema(description = "ID do usuário")
        UUID id,

        @Schema(description = "Email do usuário")
        String email,

        @Schema(description = "Nome do usuário")
        String name,

        @Schema(description = "Telefone")
        String phone,

        @Schema(description = "Usuário ativo")
        boolean active,

        @Schema(description = "Perfil do usuário")
        UserProfile profile,

        @Schema(description = "Data de criação")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {
}
