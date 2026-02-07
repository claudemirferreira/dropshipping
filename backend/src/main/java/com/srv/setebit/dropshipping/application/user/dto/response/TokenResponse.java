package com.srv.setebit.dropshipping.application.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta com tokens de autenticação")
public record TokenResponse(
        @Schema(description = "Access token JWT")
        String accessToken,

        @Schema(description = "Refresh token")
        String refreshToken,

        @Schema(description = "Tipo do token")
        String tokenType,

        @Schema(description = "Tempo de expiração em segundos")
        Long expiresIn
) {
    public static TokenResponse of(String accessToken, String refreshToken, Long expiresIn) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", expiresIn);
    }
}
