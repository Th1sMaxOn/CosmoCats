package com.cosmo.cats.api.error;

import org.springframework.validation.BindingResult;
import java.util.List;

public record ValidationErrorDetails(String field, String message) {
    public static List<ValidationErrorDetails> of(BindingResult br) {
        return br.getFieldErrors().stream()
            .map(fe -> new ValidationErrorDetails(fe.getField(), fe.getDefaultMessage()))
            .toList();
    }
}
