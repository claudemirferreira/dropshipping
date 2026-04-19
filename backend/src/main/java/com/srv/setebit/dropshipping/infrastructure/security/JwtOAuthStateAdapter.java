package com.srv.setebit.dropshipping.infrastructure.security;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import com.srv.setebit.dropshipping.domain.seller.exception.InvalidOAuthStateException;
import com.srv.setebit.dropshipping.domain.seller.port.OAuthStatePort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtOAuthStateAdapter implements OAuthStatePort {

    private final SecretKey key;
    private static final String CLAIM_MARKETPLACE = "marketplace";
    private static final long EXPIRATION_MS = 15 * 60 * 1000; // 15 minutos

    public JwtOAuthStateAdapter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generateState(UUID userId, MarketplaceEnum marketplace) {
        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_MARKETPLACE, marketplace.getCodigo())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    @Override
    public OAuthStatePayload validateState(String state) {
        if (state == null || state.isBlank()) {
            throw new InvalidOAuthStateException();
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(state)
                    .getPayload();

            UUID userId = UUID.fromString(claims.getSubject());
            MarketplaceEnum marketplace = MarketplaceEnum.fromCodigo(claims.get(CLAIM_MARKETPLACE, String.class));

            return new OAuthStatePayload(userId, marketplace);
        } catch (JwtException | IllegalArgumentException e) {
            // Qualquer falha na decodificação, assinatura, validade ou conversão invalida o state
            throw new InvalidOAuthStateException();
        }
    }
}
