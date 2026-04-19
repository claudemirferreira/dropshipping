package com.srv.setebit.dropshipping.application.seller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Dados OAuth do seller. JSON em snake_case.")
public record SellerResponse(
        @Schema(description = "ID do registro (PK, FK users.id)")
        UUID id,

        @Schema(description = "Usuário da aplicação (FK users.id)")
        @JsonProperty("userId")
        UUID userId,

        @Schema(description = "Marketplace")
        MarketplaceEnum marketplace,

        @Schema(description = "Access token OAuth")
        String accessToken,

        @Schema(description = "Tipo do token")
        String tokenType,

        @Schema(description = "Validade em segundos")
        int expiresIn,

        @Schema(description = "Escopos OAuth")
        String scope,

        @Schema(description = "ID da conta no marketplace (ex.: marketplace_id do ML)")
        Long marketplaceId,

        @Schema(description = "ID do usuário no marketplace (ex.: user_id retornado pelo ML)")
        Long marketplaceUserId,

        @Schema(description = "Refresh token OAuth")
        String refreshToken,

        @Schema(description = "Data de expiração do access token")
        Instant expiresAt,

        @Schema(description = "Data de criação")
        Instant createdAt,

        @Schema(description = "Data de atualização")
        Instant updatedAt
) {
}
