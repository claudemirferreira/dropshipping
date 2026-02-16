package com.srv.setebit.dropshipping.domain.user.exception;

public class UserLockedException extends RuntimeException {

    public UserLockedException(String reason) {
        super(reason != null ? "Usuário bloqueado: " + reason : "Usuário bloqueado");
    }
}
