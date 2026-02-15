package com.srv.setebit.dropshipping.domain.access.exception;

import java.util.UUID;

public class PerfilNotFoundException extends RuntimeException {

    public PerfilNotFoundException(UUID id) {
        super("Perfil não encontrado: " + id);
    }

    public PerfilNotFoundException(String code) {
        super("Perfil não encontrado: " + code);
    }
}
