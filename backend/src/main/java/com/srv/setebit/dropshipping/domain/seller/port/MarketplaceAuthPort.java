package com.srv.setebit.dropshipping.domain.seller.port;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceTokenResponse;

/**
 * Port que abstrai a chamada à API OAuth2 de um marketplace.
 * Cada marketplace terá sua própria implementação na camada de infraestrutura.
 */
public interface MarketplaceAuthPort {

    /**
     * Troca o authorization code por access_token e refresh_token.
     */
    MarketplaceTokenResponse exchangeCode(String code);

    /**
     * Renova o access_token usando o refresh_token.
     */
    MarketplaceTokenResponse refreshToken(String refreshToken);
}
