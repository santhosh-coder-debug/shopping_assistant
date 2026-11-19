
package com.ecommerce.assistant.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import jakarta.validation.ConstraintViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

 private static final Logger log =
         LoggerFactory.getLogger(GlobalExceptionHandler.class);

 @ExceptionHandler(NoSuchElementException.class)
 ProblemDetail missing(NoSuchElementException e) {
  return problem(HttpStatus.NOT_FOUND, "Not Found", e.getMessage());
 }

 @ExceptionHandler({
         IllegalArgumentException.class,
         MethodArgumentNotValidException.class,
         HandlerMethodValidationException.class,
         ConstraintViolationException.class,
         HttpMessageNotReadableException.class
 })
 ProblemDetail invalid(Exception e) {
  ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "Invalid Request",
          "The request is invalid.");
  if (e instanceof MethodArgumentNotValidException validationException) {
   Map<String, String> errors = new TreeMap<>();
   validationException.getBindingResult().getFieldErrors().forEach(error ->
           errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
   problem.setProperty("errors", errors);
  }
  return problem;
 }

 @ExceptionHandler(DataIntegrityViolationException.class)
 ProblemDetail conflict(DataIntegrityViolationException e) {
  return problem(HttpStatus.CONFLICT, "Conflict",
          "The request conflicts with an existing catalog record.");
 }

 @ExceptionHandler(Exception.class)
 ResponseEntity<ProblemDetail> unexpected(Exception e) {
  log.error("Unhandled request failure", e);
  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(problem(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                  "The request could not be completed."));
 }

 private ProblemDetail problem(HttpStatus status, String title, String detail) {
  ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
  problem.setTitle(title);
  return problem;
 }
}
