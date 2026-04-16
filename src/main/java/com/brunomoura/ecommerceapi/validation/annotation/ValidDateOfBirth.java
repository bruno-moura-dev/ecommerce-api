package com.brunomoura.ecommerceapi.validation.annotation;

import com.brunomoura.ecommerceapi.validation.validator.DateOfBirthValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateOfBirthValidator.class)
public @interface ValidDateOfBirth {

    String message() default "Invalid date of birth. Birth date must be between 18 and 125 years ago " +
            "and cannot be in the future.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
