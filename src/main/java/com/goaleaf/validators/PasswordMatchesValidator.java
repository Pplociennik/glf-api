package com.goaleaf.validators;

import com.goaleaf.entities.viewModels.accountsAndAuthorization.RegisterViewModel;

public class PasswordMatchesValidator {

    public boolean isValid(RegisterViewModel model) {
        return model.password.equals(model.matchingPassword);
    }
}
