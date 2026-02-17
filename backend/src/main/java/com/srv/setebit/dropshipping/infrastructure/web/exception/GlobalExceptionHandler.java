package com.srv.setebit.dropshipping.infrastructure.web.exception;

import com.srv.setebit.dropshipping.domain.access.exception.DuplicatePerfilCodeException;
import com.srv.setebit.dropshipping.domain.access.exception.DuplicateRotinaCodeException;
import com.srv.setebit.dropshipping.domain.access.exception.PerfilNotFoundException;
import com.srv.setebit.dropshipping.domain.access.exception.RotinaNotFoundException;
import com.srv.setebit.dropshipping.domain.product.exception.*;
import com.srv.setebit.dropshipping.domain.user.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(Instant.now(), 404, "Not Found", ex.getMessage(), null));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(Instant.now(), 409, "Conflict", ex.getMessage(), null));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(Instant.now(), 404, "Not Found", ex.getMessage(), null));
    }

    @ExceptionHandler(ProductImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductImageNotFound(ProductImageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(Instant.now(), 404, "Not Found", ex.getMessage(), null));
    }

    @ExceptionHandler({RotinaNotFoundException.class, PerfilNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleAccessNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorResponse(Instant.now(), 404, "Not Found", ex.getMessage(), null));
    }

    @ExceptionHandler({DuplicateRotinaCodeException.class, DuplicatePerfilCodeException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateAccess(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(Instant.now(), 409, "Conflict", ex.getMessage(), null));
    }

    @ExceptionHandler({DuplicateSkuException.class, DuplicateSlugException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateProduct(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ErrorResponse(Instant.now(), 409, "Conflict", ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(Instant.now(), 401, "Unauthorized", "Credenciais inválidas", null));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(Instant.now(), 401, "Unauthorized", ex.getMessage(), null));
    }

    @ExceptionHandler(UserLockedException.class)
    public ResponseEntity<ErrorResponse> handleUserLocked(UserLockedException ex) {
        return ResponseEntity.status(HttpStatus.LOCKED).body(
                new ErrorResponse(Instant.now(), 423, "Locked", ex.getMessage(), null));
    }
    
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(RateLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
                new ErrorResponse(Instant.now(), 429, "Too Many Requests", ex.getMessage(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ErrorResponse(Instant.now(), 403, "Forbidden", "Acesso negado", null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, String> errors = fieldErrors.stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ValidationErrorResponse(Instant.now(), 400, "Bad Request", "Erro de validação", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Erro inesperado", ex);
        String message = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            message = message + " | Causa: " + ex.getCause().getMessage();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse(Instant.now(), 500, "Internal Server Error", message, null));
    }

    public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {
    }

    public record ValidationErrorResponse(Instant timestamp, int status, String error, String message, Map<String, String> errors) {
    }
}
