package com.srv.setebit.dropshipping.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    private UUID id;
    private String token;
    private UUID userId;
    private Instant expiresAt;
    private boolean revoked;
    private Instant createdAt;

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
