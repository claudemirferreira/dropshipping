package com.srv.setebit.dropshipping.infrastructure.mercadolivre;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de deserialização da resposta da API OAuth2 do Mercado Livre.
 */
public record MercadoLivreTokenResponse(
        @JsonProperty("access_token")  String accessToken,
        @JsonProperty("token_type")    String tokenType,
        @JsonProperty("expires_in")    int expiresIn,
        @JsonProperty("scope")         String scope,
        @JsonProperty("user_id")       Long userId,
        @JsonProperty("refresh_token") String refreshToken
) {}
