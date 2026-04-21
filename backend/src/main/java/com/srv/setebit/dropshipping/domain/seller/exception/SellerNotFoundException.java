package com.srv.setebit.dropshipping.domain.seller.exception;

import java.util.UUID;

public class SellerNotFoundException extends RuntimeException {

    public SellerNotFoundException(UUID id) {
        super("Seller não encontrado para o usuário: " + id);
    }
}
