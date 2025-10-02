package com.cosmo.cats.api.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        pd.setType(URI.create("about:blank"));
        pd.setDetail("Validation failed for request.");
        pd.setProperty("errors", ValidationErrorDetails.of(ex.getBindingResult()));
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ProblemDetail handleNotFound(NoSuchElementException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Not Found");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        pd.setDetail("Invalid parameter: " + ex.getName());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    @ExceptionHandler(ErrorResponseException.class)
    @ResponseBody
    public ProblemDetail handleSpringErrors(ErrorResponseException ex, HttpServletRequest req) {
        ProblemDetail pd = ex.getBody();
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ProblemDetail handleAny(Exception ex, HttpServletRequest req) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Internal Server Error");
        pd.setDetail(ex.getMessage());
        pd.setProperty("path", req.getRequestURI());
        return pd;
    }
}
