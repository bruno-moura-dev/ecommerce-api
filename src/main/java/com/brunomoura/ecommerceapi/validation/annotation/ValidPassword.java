package com.brunomoura.ecommerceapi.validation.annotation;

import com.brunomoura.ecommerceapi.validation.validator.PasswordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {

    String message() default "Weak password. The provided password does not meet the minimum security requirements";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
