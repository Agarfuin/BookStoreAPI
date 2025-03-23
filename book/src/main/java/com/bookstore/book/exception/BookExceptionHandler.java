package com.bookstore.book.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@RestControllerAdvice
@Slf4j
public class BookExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<Object> defaultErrorHandler(Exception e) {
    return new ResponseEntity<>(
        ExceptionResponse.builder().time(LocalDateTime.now()).error(e.getMessage()).build(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Object> methodArgumentNotValidHandler(MethodArgumentNotValidException e) {
    Map<String, String> errors =
        e.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    error ->
                        error.getDefaultMessage() != null
                            ? error.getDefaultMessage()
                            : "Validation failed"));

    return ResponseEntity.badRequest()
        .body(
            ExceptionResponse.builder()
                .time(LocalDateTime.now())
                .error("Validation failed")
                .errors(errors)
                .build());
  }

  @ExceptionHandler(value = ResponseStatusException.class)
  public ResponseEntity<Object> responseStatusExceptionHandler(ResponseStatusException ex) {
    return new ResponseEntity<>(
        ExceptionResponse.builder().error(ex.getReason()).time(LocalDateTime.now()).build(),
        ex.getStatusCode());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> errors =
        ex.getConstraintViolations().stream()
            .collect(
                Collectors.toMap(
                    violation -> {
                      String path = violation.getPropertyPath().toString();
                      return path.substring(path.lastIndexOf('.') + 1);
                    },
                    ConstraintViolation::getMessage));

    return ResponseEntity.badRequest()
        .body(
            ExceptionResponse.builder()
                .time(LocalDateTime.now())
                .error("Validation failed")
                .errors(errors)
                .build());
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    String errorMessage = ex.getMostSpecificCause().getMessage();
    Map<String, String> errors = new HashMap<>();

    Pattern pattern = Pattern.compile("null value in column \\\"(\\w+)\\\"");
    Matcher matcher = pattern.matcher(errorMessage);
    if (matcher.find()) {
      String field = matcher.group(1);
      errors.put(field, "must not be null");
    }

    return ResponseEntity.badRequest()
        .body(
            ExceptionResponse.builder()
                .time(LocalDateTime.now())
                .error("Database constraint violation")
                .errors(errors)
                .build());
  }
}
