package com.sporty.jackpot.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(JackpotNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleJackpotNotFound(JackpotNotFoundException ex) {
        log.warn("Jackpot not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ErrorCode.JACKPOT_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ContributionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleContributionNotFound(ContributionNotFoundException ex) {
        log.warn("Contribution not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, ErrorCode.CONTRIBUTION_NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_ARGUMENT, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());
        return build(HttpStatus.CONFLICT, ErrorCode.INVALID_ARGUMENT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (first, second) -> first));
        log.warn("Validation failed: {}", details);
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .errorCode(ErrorCode.VALIDATION_ERROR.name())
                .message("Validation failed")
                .correlationId(correlationId())
                .timestamp(LocalDateTime.now())
                .details(details)
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, ErrorCode errorCode, String message) {
        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .errorCode(errorCode.name())
                .message(message)
                .correlationId(correlationId())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private String correlationId() {
        return MDC.get("correlationId");
    }
}
