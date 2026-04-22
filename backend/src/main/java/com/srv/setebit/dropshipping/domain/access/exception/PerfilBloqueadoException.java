package com.srv.setebit.dropshipping.domain.access.exception;

import java.util.UUID;

public class PerfilBloqueadoException extends RuntimeException {

    public PerfilBloqueadoException(UUID id) {
        super("Perfil " + id + " é padrão do sistema e não pode ser editado.");
    }
}
