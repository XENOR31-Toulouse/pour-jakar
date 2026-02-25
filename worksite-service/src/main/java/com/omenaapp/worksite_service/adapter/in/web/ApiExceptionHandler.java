package com.omenaapp.worksite_service.adapter.in.web;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage(), "ts", Instant.now().toString()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> forbidden(IllegalStateException ex) {
    if ("NOT_ASSIGNED".equals(ex.getMessage())) {
      return ResponseEntity.status(403).body(Map.of("message", "NOT_ASSIGNED"));
    }
    return ResponseEntity.status(409).body(Map.of("message", ex.getMessage()));
  }
}
