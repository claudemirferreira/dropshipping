package com.srv.setebit.dropshipping.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String passwordHash;
    private String name;
    private String phone;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private int failedLoginAttempts;
    private boolean locked;
    private String lockedReason;
    private Instant lockedAt;
    private Instant unlockedAt;
}
