package com.srv.setebit.dropshipping.application.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

@Schema(description = "Dados para atualização de usuário")
public record UpdateUserRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Length(max = 255)
        @Schema(description = "Nome completo", example = "João Silva", required = true)
        String name,

        @Length(max = 50)
        @Schema(description = "Telefone")
        String phone
) {
}
