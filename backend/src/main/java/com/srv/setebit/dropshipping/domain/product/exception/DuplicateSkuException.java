package com.srv.setebit.dropshipping.domain.product.exception;

public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String sku) {
        super("SKU já cadastrado: " + sku);
    }
}
