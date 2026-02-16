package com.srv.setebit.dropshipping.application.user.port;

import com.srv.setebit.dropshipping.domain.user.User;

import java.util.List;
import java.util.UUID;

public interface JwtProviderPort {

    /** Gera o access token. perfilCodes: códigos dos perfis do usuário (para claim "profile" / "perfilCodes"). */
    String generateAccessToken(User user, List<String> perfilCodes);
    String generateAccessTokenWithFlags(User user, boolean needsPasswordChange);

    String generateRefreshToken(User user);

    UUID getUserIdFromToken(String token);

    boolean validateToken(String token);
}
