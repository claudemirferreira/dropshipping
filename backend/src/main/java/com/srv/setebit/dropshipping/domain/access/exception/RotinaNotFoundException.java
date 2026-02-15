package com.srv.setebit.dropshipping.domain.access.exception;

import java.util.UUID;

public class RotinaNotFoundException extends RuntimeException {

    public RotinaNotFoundException(UUID id) {
        super("Rotina não encontrada: " + id);
    }

    public RotinaNotFoundException(String code) {
        super("Rotina não encontrada: " + code);
    }
}
