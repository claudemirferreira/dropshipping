package com.srv.setebit.dropshipping.domain.access.exception;

public class DuplicatePerfilCodeException extends RuntimeException {

    public DuplicatePerfilCodeException(String code) {
        super("Já existe perfil com o código: " + code);
    }
}
