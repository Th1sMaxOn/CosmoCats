package com.cosmo.cats.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class CosmicWordCheckValidator implements ConstraintValidator<CosmicWordCheck, String> {
    private static final Set<String> TERMS = Set.of("star","galaxy","comet","nebula","cosmo","orbit","meteor","astro");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true; // @NotNull handles null
        String lower = value.toLowerCase();
        return TERMS.stream().anyMatch(lower::contains);
    }
}
