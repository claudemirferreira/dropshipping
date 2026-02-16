package com.srv.setebit.dropshipping.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryPassword {
    private UUID id;
    private UUID userId;
    private String passwordHash;
    private Instant expiresAt;
    private boolean used;
    private Instant createdAt;
}
