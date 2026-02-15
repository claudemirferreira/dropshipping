package com.srv.setebit.dropshipping.application.access.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Dados para atualização de rotina")
public record UpdateRotinaRequest(
        @NotBlank(message = "Código é obrigatório")
        @Length(max = 100)
        @Schema(description = "Código único da rotina", required = true)
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

        @Length(max = 30)
        @Schema(description = "Rota da página no frontend")
        String path,

        @Schema(description = "Ativa")
        Boolean active
) {
}
