package com.srv.setebit.dropshipping.infrastructure.mercadolivre;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mercadolivre")
public record MercadoLivreProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String baseUrl,
        String authUrl,
        long tokenRefreshThresholdSeconds
) {}
