package com.srv.setebit.dropshipping.domain.product.exception;

public class InvalidStockException extends RuntimeException {
    public InvalidStockException() {
        super("Estoque mínimo não pode ser maior que o estoque atual");
    }
}

