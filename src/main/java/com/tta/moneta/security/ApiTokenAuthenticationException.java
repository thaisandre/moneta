package com.tta.moneta.security;

import org.springframework.security.core.AuthenticationException;

class ApiTokenAuthenticationException extends AuthenticationException {

    public ApiTokenAuthenticationException(String message) {
        super(message);
    }
}
