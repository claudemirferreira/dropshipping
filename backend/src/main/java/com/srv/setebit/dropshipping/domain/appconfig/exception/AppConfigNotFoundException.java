package com.srv.setebit.dropshipping.domain.appconfig.exception;

import java.util.UUID;

public class AppConfigNotFoundException extends RuntimeException {

    public AppConfigNotFoundException(UUID id) {
        super("Config não encontrada: " + id);
    }
}
