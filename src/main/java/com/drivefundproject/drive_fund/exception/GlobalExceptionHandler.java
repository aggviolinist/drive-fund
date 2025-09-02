package com.drivefundproject.drive_fund.exception;

import java.util.LinkedHashMap;
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

    //Throws IllegalArgumentException when it gets a bad error
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseHandler.generateResponse( HttpStatus.BAD_REQUEST,ex.getMessage(),null);
    }
    //Login exception to imporove security, tells user either email or password is wrong not exactly what is wrong
     @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        return ResponseHandler.generateResponse(HttpStatus.UNAUTHORIZED,invalidCredentialsMessage, null);
    }

    //Exception for Registration, in input fields, it prints out text box bringing error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST,"Validation failed",errors);
    }

    //User not found exception
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        if (ex.getMessage().contains("User not found")) {
            return ResponseHandler.generateResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        }
        return ResponseHandler.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
    }
}