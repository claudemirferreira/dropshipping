package com.srv.setebit.dropshipping.domain.user.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Já existe um usuário cadastrado com o email: " + email);
    }
}
