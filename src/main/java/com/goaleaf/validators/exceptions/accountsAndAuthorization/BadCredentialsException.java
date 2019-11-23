package com.goaleaf.validators.exceptions.accountsAndAuthorization;

import javax.security.sasl.AuthenticationException;

public class BadCredentialsException extends AuthenticationException {
    public BadCredentialsException(final String message) {
        super(message);
    }
}
