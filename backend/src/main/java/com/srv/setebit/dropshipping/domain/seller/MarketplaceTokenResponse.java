package com.srv.setebit.dropshipping.domain.seller;

/**
 * Resposta normalizada da troca de code por token em qualquer marketplace.
 */
public record MarketplaceTokenResponse(
        String accessToken,
        String tokenType,
        int expiresIn,
        String scope,
        Long marketplaceUserId,
        String refreshToken
) {}
