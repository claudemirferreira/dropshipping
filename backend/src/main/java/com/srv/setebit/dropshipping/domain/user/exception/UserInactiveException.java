package com.srv.setebit.dropshipping.domain.user.exception;

public class UserInactiveException extends RuntimeException {

    public UserInactiveException(String usuario) {
        super("Usuário inativo: " + usuario);
    }
}
