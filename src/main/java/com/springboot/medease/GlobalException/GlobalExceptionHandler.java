package com.springboot.medease.GlobalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateResourceException(DuplicateResourceException ex) {
        logger.error("Duplicate resource error: {}", ex.getMessage(), ex);
        return buildErrorResponse("Duplicate resource", ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage(), ex);

        StringBuilder details = new StringBuilder();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> details.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));

        return buildErrorResponse("Validation error", details.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({io.jsonwebtoken.JwtException.class, BadCredentialsException.class})
    public ResponseEntity<Map<String, String>> handleJwtException(Exception ex) {
        logger.error("JWT / authentication error: {}", ex.getMessage(), ex);
        return buildErrorResponse("JWT / authentication error", ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(QueueException.class)
    public ResponseEntity<Map<String, String>> handleQueueException(QueueException ex) {
        logger.error("Queue error: {}", ex.getMessage(), ex);
        return buildErrorResponse("Queue error", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // Handle role-based access errors (doctor/admin/patient)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        logger.error("Access denied: {}", ex.getMessage(), ex);
        return buildErrorResponse("Access denied", ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        logger.error("Internal server error: {}", ex.getMessage(), ex);
        return buildErrorResponse("Internal server error", ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, String>> buildErrorResponse(String error, String details, HttpStatus status) {
        Map<String, String> body = new HashMap<>();
        body.put("error", error);
        body.put("details", details);
        return ResponseEntity.status(status).body(body);
    }
}
