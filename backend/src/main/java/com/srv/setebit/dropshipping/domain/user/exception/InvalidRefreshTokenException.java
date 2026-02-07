package com.srv.setebit.dropshipping.domain.user.exception;

public class InvalidRefreshTokenException extends RuntimeException {

    public InvalidRefreshTokenException() {
        super("Refresh token inválido ou expirado");
    }
}
