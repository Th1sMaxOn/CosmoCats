package org.example.cosmocats.web.exception;

import org.example.cosmocats.feature.exception.FeatureNotAvailableException;
import org.example.cosmocats.repository.exception.PersistenceException;
import org.example.cosmocats.service.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 1. Фіча вимкнена
    @ExceptionHandler(FeatureNotAvailableException.class)
    public ProblemDetail handleFeatureNotAvailable(FeatureNotAvailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage()
        );
        problem.setTitle("Feature Not Available");
        problem.setType(URI.create("errors/feature-disabled"));
        return problem;
    }

    // 2. Не знайдено (Product, Category або Order)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("errors/resource-not-found"));
        return problem;
    }

    // 3. Помилки бази даних
    @ExceptionHandler(PersistenceException.class)
    public ProblemDetail handlePersistenceException(PersistenceException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage()
        );
        problem.setTitle("Database Error");
        if (ex.getCause() != null) {
            problem.setProperty("root_cause", ex.getCause().getMessage());
        }
        return problem;
    }

    // 4. Помилки валідації
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            org.springframework.web.bind.MethodArgumentNotValidException ex,
            org.springframework.http.HttpHeaders headers,
            org.springframework.http.HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request) {

        StringBuilder details = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            details.append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        });

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Validation failed"
        );
        problem.setTitle("Invalid Request Content");
        problem.setProperty("errors", details.toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "Access Denied"
        );
        problem.setTitle("Forbidden");
        return problem;
    }

    // 5. Всі інші помилки
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage() != null ? ex.getMessage() : "Unknown error"
        );
        problem.setTitle("Internal Server Error");
        return problem;
    }
}