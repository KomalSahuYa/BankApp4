package com.bankapp.api.exceptions;



import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================================
    // Business Exceptions
    // ================================

    @ExceptionHandler(AccountNotFoundException.class)
    public ProblemDetail handleAccountNotFound(AccountNotFoundException ex) {

        return buildProblem(
                HttpStatus.NOT_FOUND,
                "Account Error",
                ex.getMessage());
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ProblemDetail handleEmployeeNotFound(EmployeeNotFoundException ex) {

        return buildProblem(
                HttpStatus.NOT_FOUND,
                "Employee Error",
                ex.getMessage());
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ProblemDetail handleDuplicateUser(DuplicateUsernameException ex) {

        return buildProblem(
                HttpStatus.CONFLICT,
                "Duplicate Resource",
                ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ProblemDetail handleBalance(InsufficientBalanceException ex) {

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Transaction Error",
                ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleGenericBusiness(BusinessException ex) {

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Business Error",
                ex.getMessage());
    }

    // ================================
    // Validation Errors
    // ================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
          .getFieldErrors()
          .forEach(error ->
              errors.put(
                  error.getField(),
                  error.getDefaultMessage()
              ));

        ProblemDetail pd =
                ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        pd.setTitle("Validation Failed");
        pd.setDetail("Invalid request data");
        pd.setProperty("errors", errors);
        pd.setProperty("timestamp", Instant.now());

        return pd;
    }

    // ================================
    // Illegal Argument
    // ================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArg(IllegalArgumentException ex) {

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid Argument",
                ex.getMessage());
    }

    // ================================
    // Catch-All Fallback
    // ================================

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnknown(Exception ex) {

        return buildProblem(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Server Error",
                "Unexpected error occurred");
    }

    // ================================
    // Helper Builder
    // ================================

    private ProblemDetail buildProblem(
            HttpStatus status,
            String title,
            String detail) {

        ProblemDetail pd =
                ProblemDetail.forStatus(status);

        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setProperty("timestamp", Instant.now());

        return pd;
    }

}