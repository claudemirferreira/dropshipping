package com.srv.setebit.dropshipping.application.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitação de senha temporária")
public record ForgotPasswordRequest(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Schema(description = "Email do usuário", example = "admin@dropshipping.com", required = true)
        String email
) {
}
