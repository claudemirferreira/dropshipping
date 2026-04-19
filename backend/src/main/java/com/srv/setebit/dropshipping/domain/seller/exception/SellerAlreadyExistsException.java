package com.srv.setebit.dropshipping.domain.seller.exception;

import java.util.UUID;

public class SellerAlreadyExistsException extends RuntimeException {

    public SellerAlreadyExistsException(UUID userId) {
        super("Já existe registro de seller para o usuário: " + userId);
    }
}
