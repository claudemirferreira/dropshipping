package com.srv.setebit.dropshipping.domain.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    private UUID id;
    private UUID userId;
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private String scope;
    private Long marketplaceId;
    private Long marketplaceUserId;
    private MarketplaceEnum marketplace;
    private String refreshToken;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant updatedAt;

    public void registerForUser() {
        setCreatedAt(Instant.now());
        setUpdatedAt(getCreatedAt());
        setId(UUID.randomUUID());
    }

    public boolean isTokenExpired() {
        return expiresAt == null || Instant.now().isAfter(expiresAt);
    }

    public void applyTokenResponse(MarketplaceTokenResponse tokenResponse) {
        this.accessToken = tokenResponse.accessToken();
        this.refreshToken = tokenResponse.refreshToken();
        this.tokenType = tokenResponse.tokenType();
        this.expiresIn = tokenResponse.expiresIn();
        this.scope = tokenResponse.scope();
        this.marketplaceUserId = tokenResponse.marketplaceUserId();
        this.expiresAt = Instant.now().plusSeconds(tokenResponse.expiresIn());
        this.updatedAt = Instant.now();
    }

    public void validate() {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(accessToken, "accessToken");
        Objects.requireNonNull(marketplaceId, "marketplaceId");
        Objects.requireNonNull(marketplace, "marketplace");
    }
}
