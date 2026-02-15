package com.srv.setebit.dropshipping.domain.product.exception;

import java.util.UUID;

public class ProductImageNotFoundException extends RuntimeException {

    public ProductImageNotFoundException(UUID id) {
        super("Imagem do produto não encontrada: " + id);
    }
}
