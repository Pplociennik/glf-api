package com.goaleaf.validators.exceptions.accountsAndAuthorization;

@SuppressWarnings("serial")
public class EmailExistsException extends RuntimeException {

    public EmailExistsException(final String message) {
        super(message);
    }

}
