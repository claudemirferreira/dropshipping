package com.srv.setebit.dropshipping.domain.access.exception;

public class PerfilEmUsoException extends RuntimeException {

    public PerfilEmUsoException() {
        super("Não é possível excluir o perfil pois está associado a usuários ou rotinas.");
    }
}
