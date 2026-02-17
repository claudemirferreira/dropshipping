package com.srv.setebit.dropshipping.domain.user.exception;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException() {
        super("Muitas solicitações. Tente novamente mais tarde.");
    }
}
