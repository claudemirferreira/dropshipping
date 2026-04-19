package com.srv.setebit.dropshipping.infrastructure.mercadolivre;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceTokenResponse;
import com.srv.setebit.dropshipping.domain.seller.port.MarketplaceAuthPort;
import com.srv.setebit.dropshipping.infrastructure.client.MercadoLivreClient;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class MercadoLivreAuthAdapter implements MarketplaceAuthPort {

    private final MercadoLivreClient mercadoLivreClient;
    private final MercadoLivreProperties properties;

    public MercadoLivreAuthAdapter(MercadoLivreClient mercadoLivreClient,
                                   MercadoLivreProperties properties) {
        this.mercadoLivreClient = mercadoLivreClient;
        this.properties = properties;
    }

    @Override
    public MarketplaceTokenResponse exchangeCode(String code) {
        MultiValueMap<String, String> body = buildAuthorizationCodeBody(code);
        return toMarketplaceTokenResponse(mercadoLivreClient.exchangeToken(body));
    }

    @Override
    public MarketplaceTokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> body = buildRefreshTokenBody(refreshToken);
        return toMarketplaceTokenResponse(mercadoLivreClient.exchangeToken(body));
    }

    private MultiValueMap<String, String> buildAuthorizationCodeBody(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type",    "authorization_code");
        body.add("client_id",     properties.clientId());
        body.add("client_secret", properties.clientSecret());
        body.add("code",          code);
        body.add("redirect_uri",  properties.redirectUri());
        return body;
    }

    private MultiValueMap<String, String> buildRefreshTokenBody(String refreshToken) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type",    "refresh_token");
        body.add("client_id",     properties.clientId());
        body.add("client_secret", properties.clientSecret());
        body.add("refresh_token", refreshToken);
        return body;
    }

    private MarketplaceTokenResponse toMarketplaceTokenResponse(MercadoLivreTokenResponse response) {
        return new MarketplaceTokenResponse(
                response.accessToken(),
                response.tokenType(),
                response.expiresIn(),
                response.scope(),
                response.userId(),
                response.refreshToken()
        );
    }
}
