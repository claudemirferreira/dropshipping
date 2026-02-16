package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.domain.user.Bloqueio;
import com.srv.setebit.dropshipping.domain.user.BloqueioStatus;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.port.BloqueioRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class LoginAttemptService {

    private final UserRepositoryPort userRepository;
    private final BloqueioRepositoryPort bloqueioRepository;

    public LoginAttemptService(UserRepositoryPort userRepository,
                               BloqueioRepositoryPort bloqueioRepository) {
        this.userRepository = userRepository;
        this.bloqueioRepository = bloqueioRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerFailed(User user, String loginEmail) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        user.setUpdatedAt(Instant.now());

        if (attempts >= 3 && !user.isLocked()) {
            user.setLocked(true);
            user.setLockedReason("Senha inválida 3 vezes");
            user.setLockedAt(Instant.now());

            Bloqueio b = new Bloqueio();
            b.setId(UUID.randomUUID());
            b.setUserId(user.getId());
            b.setLogin(loginEmail != null ? loginEmail : user.getEmail());
            b.setMotivo("Senha inválida 3 vezes");
            b.setStatus(BloqueioStatus.ATIVO);
            b.setDataDoBloqueio(Instant.now());
            b.setCreatedAt(Instant.now());
            bloqueioRepository.save(b);
        }

        userRepository.save(user);
    }
}
