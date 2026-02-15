package com.srv.setebit.dropshipping.application.access.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.Set;
import java.util.UUID;

@Schema(description = "Dados para atualização de perfil")
public record UpdatePerfilRequest(
        @NotBlank(message = "Código é obrigatório")
        @Length(max = 50)
        @Schema(description = "Código único do perfil", required = true)
        String code,

        @NotBlank(message = "Nome é obrigatório")
        @Length(max = 255)
        @Schema(description = "Nome legível", required = true)
        String name,

        @Length(max = 500)
        @Schema(description = "Descrição")
        String description,

        @Length(max = 100)
        @Schema(description = "Ícone (classe CSS)")
        String icon,

        @Schema(description = "Ativo")
        Boolean active,

        @Schema(description = "IDs das rotinas do perfil")
        Set<UUID> rotinaIds
) {
}
