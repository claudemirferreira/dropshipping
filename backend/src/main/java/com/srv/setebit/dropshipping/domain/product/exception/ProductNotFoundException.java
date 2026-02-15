package com.srv.setebit.dropshipping.domain.product.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(UUID id) {
        super("Produto não encontrado: " + id);
    }

    public ProductNotFoundException(String slug) {
        super("Produto não encontrado com slug: " + slug);
    }
}
