package com.srv.setebit.dropshipping.application.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Definição de nova senha após login com senha temporária")
public record NewPasswordRequest(
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Schema(description = "Nova senha", example = "NovaSenha@123", required = true)
    String newPassword
) {}