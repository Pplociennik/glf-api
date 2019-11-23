package com.goaleaf.validators;

public class HabitTitleValidator {

    public boolean isValid(String title) {
        return (title.length() <= 50 && title.length() >= 5);
    }
}
