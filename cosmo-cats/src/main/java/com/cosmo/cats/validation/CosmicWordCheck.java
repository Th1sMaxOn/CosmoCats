package com.cosmo.cats.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CosmicWordCheckValidator.class)
public @interface CosmicWordCheck {
    String message() default "Name must contain a cosmic term (e.g., star, galaxy, comet, nebula).";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
