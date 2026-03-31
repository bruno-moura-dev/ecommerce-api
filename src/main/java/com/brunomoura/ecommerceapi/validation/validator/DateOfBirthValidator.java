package com.brunomoura.ecommerceapi.validation.validator;

import com.brunomoura.ecommerceapi.validation.annotation.ValidDateOfBirth;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateOfBirthValidator implements ConstraintValidator<ValidDateOfBirth, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate maxBirthDate = currentDate.minusYears(125);
        LocalDate minBirthDate = currentDate.minusYears(18);

        return !value.isBefore(maxBirthDate) && !value.isAfter(minBirthDate);
    }
}
