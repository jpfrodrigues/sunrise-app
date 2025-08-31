package com.sunrise.sunriseapp.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidLocationException.class)
    public ResponseEntity<ApiError> handleInvalidLocation(InvalidLocationException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest().body(
                new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Invalid location", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ApiError> handleExternal(ExternalApiException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                new ApiError(Instant.now(), HttpStatus.BAD_GATEWAY.value(), "External API failure", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String msg = ex.getBindingResult().getAllErrors().stream().findFirst().map(o -> o.getDefaultMessage()).orElse("Validation error");
        return ResponseEntity.badRequest().body(
                new ApiError(Instant.now(), HttpStatus.BAD_REQUEST.value(), "Validation error", msg, req.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiError(Instant.now(), 500, "Server error", ex.getMessage(), req.getRequestURI()));
    }
}
