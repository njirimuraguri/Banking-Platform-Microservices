package com.banking.card_service.exception;

import com.banking.card_service.dto.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<StandardResponse<Object>> handleNotFound(NoSuchElementException ex) {
        return buildResponse(" " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(" Bad input: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Object>> handleGeneric(Exception ex) {
        return buildResponse(" Unexpected error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private ResponseEntity<StandardResponse<Object>> buildResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(StandardResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(null)
                .build());
    }
}
