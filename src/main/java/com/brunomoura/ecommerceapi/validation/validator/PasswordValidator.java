package com.brunomoura.ecommerceapi.validation.validator;

import com.brunomoura.ecommerceapi.validation.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return true;
        }

        if (value.length() < 8 || value.length() > 128) {
            return false;
        }

        if (!value.matches(".*[a-z].*")) {
            return false;
        }

        if (!value.matches(".*[A-Z].*")) {
            return false;
        }

        if (!value.matches(".*[0-9].*")) {
            return false;
        }

        if (!value.matches(".*[^a-zA-Z0-9].*")) {
            return false;
        }

        return true;
    }


}
