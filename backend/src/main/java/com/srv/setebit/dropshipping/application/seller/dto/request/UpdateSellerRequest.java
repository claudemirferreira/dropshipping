package com.srv.setebit.dropshipping.application.seller.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Atualização dos tokens do seller (snake_case aceito)")
public record UpdateSellerRequest(
        @NotNull(message = "marketplace é obrigatório")
        @Schema(description = "Marketplace (mercado_livre, shopee)", requiredMode = Schema.RequiredMode.REQUIRED)
        MarketplaceEnum marketplace,

        @NotBlank(message = "access_token é obrigatório")
        @JsonProperty("access_token")
        @JsonAlias("accessToken")
        @Schema(description = "Access token OAuth", requiredMode = Schema.RequiredMode.REQUIRED)
        String accessToken,

        @NotBlank(message = "token_type é obrigatório")
        @JsonProperty("token_type")
        @JsonAlias("tokenType")
        @Schema(description = "Tipo do token (ex.: Bearer)", requiredMode = Schema.RequiredMode.REQUIRED)
        String tokenType,

        @NotNull(message = "expires_in é obrigatório")
        @JsonProperty("expires_in")
        @JsonAlias("expiresIn")
        @Schema(description = "Validade em segundos", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer expiresIn,

        @NotBlank(message = "scope é obrigatório")
        @Schema(description = "Escopos OAuth", requiredMode = Schema.RequiredMode.REQUIRED)
        String scope,

        @NotNull(message = "marketplace_id é obrigatório")
        @JsonProperty("marketplace_id")
        @JsonAlias({"user_id", "mercadoLivreUserId", "marketplaceId"})
        @Schema(description = "ID da conta no marketplace", requiredMode = Schema.RequiredMode.REQUIRED)
        Long marketplaceId,

        @NotBlank(message = "refresh_token é obrigatório")
        @JsonProperty("refresh_token")
        @JsonAlias("refreshToken")
        @Schema(description = "Refresh token OAuth", requiredMode = Schema.RequiredMode.REQUIRED)
        String refreshToken
) {
}
