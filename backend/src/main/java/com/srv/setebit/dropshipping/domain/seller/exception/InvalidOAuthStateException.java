package com.srv.setebit.dropshipping.domain.seller.exception;

public class InvalidOAuthStateException extends RuntimeException {
    public InvalidOAuthStateException() {
        super("Sessão de autorização inválida ou expirada. Por favor, inicie a conexão novamente.");
    }
}
