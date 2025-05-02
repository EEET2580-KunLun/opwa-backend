package eeet2580.kunlun.opwa.backend.common.handler;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseRes<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        BaseRes<String> response = new BaseRes<>(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<BaseRes<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        BaseRes<Map<String, String>> response = new BaseRes<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseRes<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        BaseRes<Void> response = new BaseRes<>(HttpStatus.FORBIDDEN.value(), "Access denied: insufficient permissions", null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseRes<Void>> handleAuthenticationException(AuthenticationException ex) {
        BaseRes<Void> response = new BaseRes<>(HttpStatus.UNAUTHORIZED.value(), "Authentication failed", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseRes<Void>> handleGeneralException(Exception ex) {
        BaseRes<Void> response = new BaseRes<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<BaseRes<Void>> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        BaseRes<Void> response = new BaseRes<>(
                HttpStatus.BAD_REQUEST.value(),
                "Missing required file: " + ex.getRequestPartName(),
                null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}