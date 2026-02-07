package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.domain.user.port.RefreshTokenRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class LogoutUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepository;

    public LogoutUseCase(RefreshTokenRepositoryPort refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void execute(UUID userId) {
        refreshTokenRepository.revokeByUserId(userId);
    }
}
