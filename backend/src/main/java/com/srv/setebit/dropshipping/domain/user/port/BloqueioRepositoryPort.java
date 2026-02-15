package com.srv.setebit.dropshipping.domain.user.port;

import com.srv.setebit.dropshipping.domain.user.Bloqueio;
import com.srv.setebit.dropshipping.domain.user.BloqueioStatus;

import java.util.Optional;
import java.util.UUID;

public interface BloqueioRepositoryPort {
    Bloqueio save(Bloqueio bloqueio);
    Optional<Bloqueio> findActiveByUserId(UUID userId);
    void closeActiveByUserId(UUID userId, UUID auditorId, boolean selfService);
}
