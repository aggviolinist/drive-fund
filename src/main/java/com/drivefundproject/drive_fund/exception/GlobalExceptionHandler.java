package com.drivefundproject.drive_fund.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.drivefundproject.drive_fund.dto.Response.ResponseHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${error.auth.invalid_credentials}")
    private String invalidCredentialsMessage;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseHandler.generateResponse( HttpStatus.BAD_REQUEST,ex.getMessage(),null);
    }
     @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,invalidCredentialsMessage, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST,"Validation failed",errors);
    }
}