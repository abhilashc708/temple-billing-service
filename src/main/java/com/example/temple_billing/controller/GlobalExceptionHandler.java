package com.example.temple_billing.controller;

import com.example.temple_billing.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        logger.warn("Resource not found exception: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("Resource not found", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Illegal argument exception: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("Invalid argument", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Customize status based on message
        if (message != null) {
            if (message.contains("not found")) {
                status = HttpStatus.NOT_FOUND;
                logger.warn("Runtime exception - Resource not found: {}", message);
            } else if (message.contains("Access denied") || message.contains("Access Denied")) {
                status = HttpStatus.FORBIDDEN;
                logger.warn("Runtime exception - Access denied: {}", message);
            } else if (message.contains("Invalid") || message.contains("Bad request")) {
                status = HttpStatus.BAD_REQUEST;
                logger.warn("Runtime exception - Bad request: {}", message);
            } else {
                logger.error("Runtime exception: {}", message, ex);
            }
        } else {
            logger.error("Runtime exception occurred", ex);
        }

        ErrorResponse error = new ErrorResponse("Runtime error", message);
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.warn("Access denied exception: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse("Access denied", "You do not have permission to access this resource");
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            message.append(fieldName).append(" - ").append(errorMessage).append("; ");
        });
        logger.warn("Validation exception: {}", message.toString());
        ErrorResponse error = new ErrorResponse("Validation error", message.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        StringBuilder message = new StringBuilder("Validation failed: ");
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            message.append(violation.getMessage()).append("; ");
        }
        logger.warn("Constraint violation exception: {}", message.toString());
        ErrorResponse error = new ErrorResponse("Validation error", message.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        logger.error("Unexpected exception occurred", ex);
        ErrorResponse error = new ErrorResponse("Internal server error", "An unexpected error occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
