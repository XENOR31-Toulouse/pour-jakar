package com.omenaapp.auth_service.adapter.in.web;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

  record ApiError(String code, String message, Instant timestamp) {}

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiError("BAD_REQUEST", ex.getMessage(), Instant.now()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiError> conflict(IllegalStateException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ApiError("CONFLICT", ex.getMessage(), Instant.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> internalServerError(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiError("INTERNAL_SERVER_ERROR", "An unexpected error occurred", Instant.now()));
  }

  
}
