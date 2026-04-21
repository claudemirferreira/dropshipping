package com.srv.setebit.dropshipping.domain.user.port;

import com.srv.setebit.dropshipping.domain.user.TemporaryPassword;

import java.util.Optional;
import java.util.UUID;

public interface TemporaryPasswordRepositoryPort {
    TemporaryPassword save(TemporaryPassword temp);
    Optional<TemporaryPassword> findActiveByUserId(UUID userId);
    Optional<TemporaryPassword> findLatestByUserId(UUID userId);
    void markUsed(UUID id);
    void deleteByUserId(UUID userId);
}
