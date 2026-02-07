package com.srv.setebit.dropshipping.domain.user.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UUID id) {
        super("Usuário não encontrado com id: " + id);
    }

    public UserNotFoundException(String email) {
        super("Usuário não encontrado com email: " + email);
    }
}
