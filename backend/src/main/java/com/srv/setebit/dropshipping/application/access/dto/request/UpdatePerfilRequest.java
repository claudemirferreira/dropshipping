package com.srv.setebit.dropshipping.application.access.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Set;
import java.util.UUID;

@Builder
@Schema(description = "Dados para atualização de perfil")
public record UpdatePerfilRequest(

        @Schema(description = "Código único do perfil")
        UUID id,

        @NotBlank(message = "Código é obrigatório")
        @Length(max = 50)
        @Schema(description = "Código único do perfil", required = true)
        String code,

        @NotBlank(message = "Nome é obrigatório")
        @Length(max = 60, message = "Nome deve ter no máximo 60 caracteres")
        @Schema(description = "Nome legível", required = true)
        String name,

        @Length(max = 100)
        @Schema(description = "Ícone (classe CSS)")
        String icon,

        @Schema(description = "Ativo")
        Boolean active,

        @Schema(description = "IDs das rotinas do perfil")
        Set<UUID> rotinaIds
) {
}
