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
    private MarketplaceEnum marketplace;
    private String refreshToken;
    private Instant createdAt;
    private Instant updatedAt;

    public void registerForUser() {
        setCreatedAt(Instant.now());
        setUpdatedAt(getCreatedAt());
        setId(UUID.randomUUID());
    }

    public void validate(){
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(accessToken, "accessToken");
        Objects.requireNonNull(marketplaceId, "marketplaceId");
        Objects.requireNonNull(marketplace, "marketplace");
    }
}
