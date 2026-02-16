package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.user.dto.request.LoginRequest;
import com.srv.setebit.dropshipping.application.user.dto.response.TokenResponse;
import com.srv.setebit.dropshipping.application.user.port.JwtProviderPort;
import com.srv.setebit.dropshipping.application.user.port.PasswordEncoderPort;
import com.srv.setebit.dropshipping.domain.user.RefreshToken;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.InvalidCredentialsException;
import com.srv.setebit.dropshipping.domain.user.port.RefreshTokenRepositoryPort;
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

    @Value("${jwt.access-token-expiration-ms:900000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    public LoginUseCase(UserRepositoryPort userRepository,
                        RefreshTokenRepositoryPort refreshTokenRepository,
                        PasswordEncoderPort passwordEncoder,
                        JwtProviderPort jwtProvider,
                        GetUserPerfisUseCase getUserPerfisUseCase) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.getUserPerfisUseCase = getUserPerfisUseCase;
    }

    @Transactional
    public TokenResponse execute(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        List<String> perfilCodes = getUserPerfisUseCase.execute(user.getId()).stream()
                .map(p -> p.code())
                .collect(Collectors.toList());
        String accessToken = jwtProvider.generateAccessToken(user, perfilCodes);
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
