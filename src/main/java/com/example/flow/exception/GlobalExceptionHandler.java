package com.example.flow.exception;

import com.example.flow.util.ApiFlowException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiFlowException.class)
  public ResponseEntity<Map<String, Object>> handleFlow(ApiFlowException e) {
    return ResponseEntity.badRequest().body(Map.of(
        "error", "FLOW_FAILED",
        "message", e.getMessage()
    ));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handle(Exception e) {
    return ResponseEntity.internalServerError().body(Map.of(
        "error", "INTERNAL_ERROR",
        "message", e.getMessage()
    ));
  }
}
