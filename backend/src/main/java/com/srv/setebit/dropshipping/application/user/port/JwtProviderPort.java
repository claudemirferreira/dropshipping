package com.srv.setebit.dropshipping.application.user.port;

import com.srv.setebit.dropshipping.domain.user.User;

import java.util.UUID;

public interface JwtProviderPort {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    UUID getUserIdFromToken(String token);

    boolean validateToken(String token);
}
