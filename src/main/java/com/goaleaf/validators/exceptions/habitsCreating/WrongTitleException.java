package com.goaleaf.validators.exceptions.habitsCreating;

import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;

public class WrongTitleException extends RuntimeException {
    public WrongTitleException(final String message) {
        super(message);
    }
}
