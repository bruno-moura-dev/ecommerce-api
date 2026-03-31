package com.brunomoura.ecommerceapi.validation.annotation;

import com.brunomoura.ecommerceapi.validation.validator.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface ValidPhoneNumber {

    String message() default "Invalid phone number format. Please provide a Brazilian phone number format.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
