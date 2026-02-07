package com.srv.setebit.dropshipping.application.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Refresh token para renovação")
public record RefreshTokenRequest(
        @NotBlank(message = "Refresh token é obrigatório")
        @Schema(description = "Refresh token", required = true)
        String refreshToken
) {
}
