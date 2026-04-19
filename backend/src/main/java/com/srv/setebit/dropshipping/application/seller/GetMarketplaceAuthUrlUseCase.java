package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import com.srv.setebit.dropshipping.domain.seller.port.OAuthStatePort;
import com.srv.setebit.dropshipping.infrastructure.mercadolivre.MercadoLivreProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * Gera a URL de autorização OAuth2 para o marketplace solicitado.
 * O frontend redireciona o usuário para essa URL.
 */
@Service
public class GetMarketplaceAuthUrlUseCase {

    private final MercadoLivreProperties mlProperties;
    private final OAuthStatePort oAuthStatePort;

    public GetMarketplaceAuthUrlUseCase(MercadoLivreProperties mlProperties, OAuthStatePort oAuthStatePort) {
        this.mlProperties = mlProperties;
        this.oAuthStatePort = oAuthStatePort;
    }

    public String execute(MarketplaceEnum marketplace, UUID userId) {
        String state = oAuthStatePort.generateState(userId, marketplace);
        return switch (marketplace) {
            case MERCADO_LIVRE -> buildMercadoLivreUrl(state);
            default -> throw new IllegalArgumentException("Marketplace sem suporte de autenticação: " + marketplace);
        };
    }

    private String buildMercadoLivreUrl(String state) {
        return UriComponentsBuilder
                .fromUriString(mlProperties.authUrl())
                .path("/authorization")
                .queryParam("response_type", "code")
                .queryParam("client_id", mlProperties.clientId())
                .queryParam("redirect_uri", mlProperties.redirectUri())
                .queryParam("state", state)
                .toUriString();
    }
}
