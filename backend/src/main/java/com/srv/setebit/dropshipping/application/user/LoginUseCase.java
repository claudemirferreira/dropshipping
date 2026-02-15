package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.user.dto.request.LoginRequest;
import com.srv.setebit.dropshipping.application.user.dto.response.TokenResponse;
import com.srv.setebit.dropshipping.application.user.port.JwtProviderPort;
import com.srv.setebit.dropshipping.application.user.port.PasswordEncoderPort;
import com.srv.setebit.dropshipping.domain.user.RefreshToken;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.Bloqueio;
import com.srv.setebit.dropshipping.domain.user.BloqueioStatus;
import com.srv.setebit.dropshipping.domain.user.TemporaryPassword;
import com.srv.setebit.dropshipping.domain.user.exception.InvalidCredentialsException;
import com.srv.setebit.dropshipping.domain.user.exception.UserLockedException;
import com.srv.setebit.dropshipping.domain.user.port.RefreshTokenRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.BloqueioRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.TemporaryPasswordRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtProviderPort jwtProvider;
    private final BloqueioRepositoryPort bloqueioRepository;
    private final TemporaryPasswordRepositoryPort tempPasswordRepository;

    @Value("${jwt.access-token-expiration-ms:900000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    public LoginUseCase(UserRepositoryPort userRepository,
                        RefreshTokenRepositoryPort refreshTokenRepository,
                        PasswordEncoderPort passwordEncoder,
                        JwtProviderPort jwtProvider,
                        BloqueioRepositoryPort bloqueioRepository,
                        TemporaryPasswordRepositoryPort tempPasswordRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.bloqueioRepository = bloqueioRepository;
        this.tempPasswordRepository = tempPasswordRepository;
    }

    @Transactional
    public TokenResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }

        if (user.isLocked()) {
            throw new UserLockedException(user.getLockedReason());
        }

        boolean needsPasswordChange = false;
        boolean valid = passwordEncoder.matches(request.password(), user.getPasswordHash());

        if (!valid) {
            // check temporary password
            var tempOpt = tempPasswordRepository.findActiveByUserId(user.getId());
            if (tempOpt.isPresent() && passwordEncoder.matches(request.password(), tempOpt.get().getPasswordHash())) {
                TemporaryPassword temp = tempOpt.get();
                tempPasswordRepository.markUsed(temp.getId());
                // unlock user via self-service
                user.setLocked(false);
                user.setUnlockedAt(Instant.now());
                user.setFailedLoginAttempts(0);
                user.setUpdatedAt(Instant.now());
                userRepository.save(user);
                bloqueioRepository.closeActiveByUserId(user.getId(), null, true);
                needsPasswordChange = true;
                valid = true;
            }
        }

        if (!valid) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);
            user.setUpdatedAt(Instant.now());
            if (attempts >= 3) {
                user.setLocked(true);
                user.setLockedReason("Senha inválida 3 vezes");
                user.setLockedAt(Instant.now());
                // cria registro de bloqueio ativo
                Bloqueio b = new Bloqueio();
                b.setId(UUID.randomUUID());
                b.setUserId(user.getId());
                b.setLogin(user.getEmail());
                b.setMotivo("Senha inválida 3 vezes");
                b.setStatus(BloqueioStatus.ATIVO);
                b.setDataDoBloqueio(Instant.now());
                b.setCreatedAt(Instant.now());
                bloqueioRepository.save(b);
            }
            userRepository.save(user);
            throw new InvalidCredentialsException();
        } else {
            // reset attempts on success
            if (user.getFailedLoginAttempts() != 0) {
                user.setFailedLoginAttempts(0);
                user.setUpdatedAt(Instant.now());
                userRepository.save(user);
            }
        }

        String accessToken = needsPasswordChange
                ? jwtProvider.generateAccessTokenWithFlags(user, true)
                : jwtProvider.generateAccessToken(user);
        String refreshTokenValue = jwtProvider.generateRefreshToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUserId(user.getId());
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));
        refreshToken.setRevoked(false);
        refreshToken.setCreatedAt(Instant.now());
        refreshTokenRepository.save(refreshToken);

        long expiresIn = accessTokenExpirationMs / 1000;
        return TokenResponse.of(accessToken, refreshTokenValue, expiresIn);
    }
}
