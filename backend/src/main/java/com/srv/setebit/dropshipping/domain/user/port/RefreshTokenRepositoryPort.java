package com.srv.setebit.dropshipping.domain.user.port;

import com.srv.setebit.dropshipping.domain.user.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void revokeByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
