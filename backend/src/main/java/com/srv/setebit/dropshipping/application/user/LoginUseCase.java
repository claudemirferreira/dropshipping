package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.user.dto.request.LoginRequest;
import com.srv.setebit.dropshipping.application.user.dto.response.TokenResponse;
import com.srv.setebit.dropshipping.application.user.port.JwtProviderPort;
import com.srv.setebit.dropshipping.application.user.port.PasswordEncoderPort;
import com.srv.setebit.dropshipping.domain.user.RefreshToken;
import com.srv.setebit.dropshipping.domain.user.TemporaryPassword;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.InvalidCredentialsException;
import com.srv.setebit.dropshipping.domain.user.exception.UserInactiveException;
import com.srv.setebit.dropshipping.domain.user.exception.UserLockedException;
import com.srv.setebit.dropshipping.domain.user.port.BloqueioRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.RefreshTokenRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.TemporaryPasswordRepositoryPort;
import com.srv.setebit.dropshipping.domain.user.port.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtProviderPort jwtProvider;
    private final GetUserPerfisUseCase getUserPerfisUseCase;
    private final BloqueioRepositoryPort bloqueioRepository;
    private final TemporaryPasswordRepositoryPort tempPasswordRepository;
    private final LoginAttemptService loginAttemptService;

    @Value("${jwt.access-token-expiration-ms:900000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    public LoginUseCase(UserRepositoryPort userRepository, RefreshTokenRepositoryPort refreshTokenRepository,
            PasswordEncoderPort passwordEncoder, JwtProviderPort jwtProvider, GetUserPerfisUseCase getUserPerfisUseCase,
            BloqueioRepositoryPort bloqueioRepository, TemporaryPasswordRepositoryPort tempPasswordRepository,
            LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.getUserPerfisUseCase = getUserPerfisUseCase;
        this.bloqueioRepository = bloqueioRepository;
        this.tempPasswordRepository = tempPasswordRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Transactional
    public TokenResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new UserInactiveException();
        }

        boolean needsPasswordChange = false;
        boolean valid = false;

        // tenta senha padrão — usuário bloqueado não pode entrar nem com a senha correta
        if (passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            if (user.isLocked()) {
                throw new UserLockedException(user.getLockedReason());
            }
            valid = true;
        } else {
            // tenta senha temporária (self-service unlock) — único caminho permitido para conta bloqueada
            var tempOpt = tempPasswordRepository.findActiveByUserId(user.getId());
            if (tempOpt.isPresent() && passwordEncoder.matches(request.password(), tempOpt.get().getPasswordHash())) {
                TemporaryPassword temp = tempOpt.get();
                tempPasswordRepository.markUsed(temp.getId());
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

        // senha errada: se bloqueado informa bloqueio; senão registra tentativa falha
        if (!valid) {
            if (user.isLocked()) {
                throw new UserLockedException(user.getLockedReason());
            }
            loginAttemptService.registerFailed(user, request.email());
            throw new InvalidCredentialsException();
        }

        // reset tentativas falhas após login bem-sucedido
        if (user.getFailedLoginAttempts() != 0) {
            user.setFailedLoginAttempts(0);
            user.setUpdatedAt(Instant.now());
            userRepository.save(user);
        }

        List<String> perfilCodes = getUserPerfisUseCase.execute(user.getId()).stream()
                .map(p -> p.code())
                .collect(Collectors.toList());
        String accessToken = needsPasswordChange
                ? jwtProvider.generateAccessTokenWithFlags(user, true)
                : jwtProvider.generateAccessToken(user, perfilCodes);
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
