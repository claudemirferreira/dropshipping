package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.UserNotFoundException;
import com.srv.setebit.dropshipping.domain.user.port.BloqueioRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class UnlockUserUseCase {

    private final UserRepositoryPort userRepository;
    private final BloqueioRepositoryPort bloqueioRepository;

    public UnlockUserUseCase(UserRepositoryPort userRepository,
                             BloqueioRepositoryPort bloqueioRepository) {
        this.userRepository = userRepository;
        this.bloqueioRepository = bloqueioRepository;
    }

    @Transactional
    public void execute(UUID targetUserId, UUID adminId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));

        user.setLocked(false);
        user.setLockedReason(null);
        user.setUnlockedAt(Instant.now());
        user.setFailedLoginAttempts(0);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        bloqueioRepository.closeActiveByUserId(targetUserId, adminId, false);
    }
}
