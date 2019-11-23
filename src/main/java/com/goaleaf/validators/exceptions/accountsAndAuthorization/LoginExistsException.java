package com.goaleaf.validators.exceptions.accountsAndAuthorization;

public class LoginExistsException extends RuntimeException {
    public LoginExistsException(final String message) {
        super(message);
    }
}
