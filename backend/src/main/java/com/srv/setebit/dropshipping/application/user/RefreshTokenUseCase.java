package com.srv.setebit.dropshipping.application.user;

import com.srv.setebit.dropshipping.application.access.GetUserPerfisUseCase;
import com.srv.setebit.dropshipping.application.user.dto.request.RefreshTokenRequest;
import com.srv.setebit.dropshipping.application.user.dto.response.TokenResponse;
import com.srv.setebit.dropshipping.application.user.port.JwtProviderPort;
import com.srv.setebit.dropshipping.domain.user.RefreshToken;
import com.srv.setebit.dropshipping.domain.user.User;
import com.srv.setebit.dropshipping.domain.user.exception.InvalidRefreshTokenException;
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
public class RefreshTokenUseCase {

    private final UserRepositoryPort userRepository;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final JwtProviderPort jwtProvider;
    private final GetUserPerfisUseCase getUserPerfisUseCase;

    @Value("${jwt.access-token-expiration-ms:900000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration-ms:604800000}")
    private long refreshTokenExpirationMs;

    public RefreshTokenUseCase(UserRepositoryPort userRepository,
                               RefreshTokenRepositoryPort refreshTokenRepository,
                               JwtProviderPort jwtProvider,
                               GetUserPerfisUseCase getUserPerfisUseCase) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtProvider = jwtProvider;
        this.getUserPerfisUseCase = getUserPerfisUseCase;
    }

    @Transactional
    public TokenResponse execute(RefreshTokenRequest request) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(InvalidRefreshTokenException::new);

        if (storedToken.isRevoked() || storedToken.isExpired()) {
            throw new InvalidRefreshTokenException();
        }

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(InvalidRefreshTokenException::new);

        if (!user.isActive()) {
            throw new InvalidRefreshTokenException();
        }

        refreshTokenRepository.revokeByUserId(user.getId());

        List<String> perfilCodes = getUserPerfisUseCase.execute(user.getId()).stream()
                .map(p -> p.code())
                .collect(Collectors.toList());
        String accessToken = jwtProvider.generateAccessToken(user, perfilCodes);
        String newRefreshTokenValue = jwtProvider.generateRefreshToken(user);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setId(UUID.randomUUID());
        newRefreshToken.setToken(newRefreshTokenValue);
        newRefreshToken.setUserId(user.getId());
        newRefreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpirationMs));
        newRefreshToken.setRevoked(false);
        newRefreshToken.setCreatedAt(Instant.now());
        refreshTokenRepository.save(newRefreshToken);

        long expiresIn = accessTokenExpirationMs / 1000;
        return TokenResponse.of(accessToken, newRefreshTokenValue, expiresIn);
    }
}
