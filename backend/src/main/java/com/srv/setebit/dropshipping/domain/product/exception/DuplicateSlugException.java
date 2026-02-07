package com.srv.setebit.dropshipping.domain.product.exception;

public class DuplicateSlugException extends RuntimeException {

    public DuplicateSlugException(String slug) {
        super("Slug já cadastrado: " + slug);
    }
}
