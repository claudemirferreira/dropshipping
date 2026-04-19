package com.srv.setebit.dropshipping.domain.seller.port;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;

import java.util.UUID;

/**
 * Port que abstrai a geração e validação do parâmetro state do OAuth2.
 * O state é usado para correlacionar o fluxo de redirecionamento, identificar o usuário
 * logado no sistema quando o callback ocorre (já que ocorre sem autorização)
 * e identificar qual marketplace está sendo vinculado.
 */
public interface OAuthStatePort {

    /**
     * Gera um state seguro para iniciar o fluxo OAuth.
     */
    String generateState(UUID userId, MarketplaceEnum marketplace);

    /**
     * Valida um state recebido no callback e extrai o userId e o marketplace originais.
     * @throws com.srv.setebit.dropshipping.domain.seller.exception.InvalidOAuthStateException se for inválido ou expirado.
     */
    OAuthStatePayload validateState(String state);

    record OAuthStatePayload(UUID userId, MarketplaceEnum marketplace) {}
}
