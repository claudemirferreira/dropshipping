package com.srv.setebit.dropshipping.domain.access.exception;

public class DuplicateRotinaCodeException extends RuntimeException {

    public DuplicateRotinaCodeException(String code) {
        super("Já existe rotina com o código: " + code);
    }
}
