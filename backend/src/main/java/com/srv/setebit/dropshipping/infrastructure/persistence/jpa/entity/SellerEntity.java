package com.srv.setebit.dropshipping.infrastructure.persistence.jpa.entity;

import com.srv.setebit.dropshipping.domain.seller.MarketplaceEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seller", uniqueConstraints = {
        @UniqueConstraint(name = "uk_seller_marketplace_pair", columnNames = {"marketplace", "id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellerEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "access_token", nullable = false, columnDefinition = "text")
    private String accessToken;

    @Column(name = "token_type", nullable = false, length = 32)
    private String tokenType;

    @Column(name = "expires_in", nullable = false)
    private int expiresIn;

    @Column(name = "scope", nullable = false, columnDefinition = "text")
    private String scope;

    @Column(name = "marketplace_id", nullable = false)
    private Long marketplaceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "marketplace", nullable = false, length = 60)
    private MarketplaceEnum marketplace;

    @Column(name = "refresh_token", nullable = false, columnDefinition = "text")
    private String refreshToken;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
