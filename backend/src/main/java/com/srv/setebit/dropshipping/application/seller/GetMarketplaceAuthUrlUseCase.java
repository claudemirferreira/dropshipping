package com.srv.setebit.dropshipping.application.seller;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import com.srv.setebit.dropshipping.infrastructure.mercadolivre.MercadoLivreProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Gera a URL de autorização OAuth2 para o marketplace solicitado.
 * O frontend redireciona o usuário para essa URL.
 */
@Service
public class GetMarketplaceAuthUrlUseCase {

    private final MercadoLivreProperties mlProperties;

    public GetMarketplaceAuthUrlUseCase(MercadoLivreProperties mlProperties) {
        this.mlProperties = mlProperties;
    }

    public String execute(MarketplaceEnum marketplace) {
        return switch (marketplace) {
            case MERCADO_LIVRE -> buildMercadoLivreUrl();
            default -> throw new IllegalArgumentException("Marketplace sem suporte de autenticação: " + marketplace);
        };
    }

    private String buildMercadoLivreUrl() {
        return UriComponentsBuilder
                .fromUriString(mlProperties.authUrl())
                .path("/authorization")
                .queryParam("response_type", "code")
                .queryParam("client_id", mlProperties.clientId())
                .queryParam("redirect_uri", mlProperties.redirectUri())
                .toUriString();
    }
}
