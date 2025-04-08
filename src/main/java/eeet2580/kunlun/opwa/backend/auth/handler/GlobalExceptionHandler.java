package eeet2580.kunlun.opwa.backend.auth.handler;

import eeet2580.kunlun.opwa.backend.common.dto.resp.BaseRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

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
    public ResponseEntity<BaseRes<String>> handleAccessDeniedException(AccessDeniedException ex) {
        BaseRes<String> response = new BaseRes<>(HttpStatus.FORBIDDEN.value(), "Access denied: insufficient permissions", null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseRes<String>> handleAuthenticationException(AuthenticationException ex) {
        BaseRes<String> response = new BaseRes<>(HttpStatus.UNAUTHORIZED.value(), "Authentication failed", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseRes<String>> handleGeneralException(Exception ex) {
        BaseRes<String> response = new BaseRes<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}