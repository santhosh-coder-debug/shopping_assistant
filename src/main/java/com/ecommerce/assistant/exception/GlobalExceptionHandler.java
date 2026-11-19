
package com.ecommerce.assistant.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

 private static final Logger log =
         LoggerFactory.getLogger(GlobalExceptionHandler.class);

 @ExceptionHandler(NoSuchElementException.class)
 ResponseEntity<Map<String, String>> missing(
         NoSuchElementException e) {

  return ResponseEntity.status(404)
          .body(Map.of("error", e.getMessage()));
 }

 @ExceptionHandler({
         IllegalArgumentException.class,
         MethodArgumentNotValidException.class
 })
 ResponseEntity<Map<String, String>> invalid(Exception e) {

  log.error("Invalid request", e);

  return ResponseEntity.badRequest()
          .body(Map.of("error", "Invalid request"));
 }
}
