package com.bookstore.apigateway.exception;

import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@RestControllerAdvice
@Slf4j
public class ApiGatewayExceptionHandler {

  @ExceptionHandler(value = Exception.class)
  public ResponseEntity<Object> defaultErrorHandler(Exception e) {
    return new ResponseEntity<>(
        ExceptionResponse.builder().time(LocalDateTime.now()).error(e.getMessage()).build(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Object> methodArgumentNotValidHandler(MethodArgumentNotValidException e) {

    Map<String, String> errorMap = new HashMap<>();
    e.getBindingResult()
        .getFieldErrors()
        .forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
    return new ResponseEntity<>(
        ExceptionResponse.builder()
            .time(LocalDateTime.now())
            .error("Constraint Validation Failed")
            .errors(errorMap)
            .build(),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(
      value = {
        JwtException.class,
        AuthenticationException.class,
        InsufficientAuthenticationException.class
      })
  public ResponseEntity<Object> jwtExceptionHandler(Exception e) {
    return new ResponseEntity<>(
        ExceptionResponse.builder().error(e.getMessage()).time(LocalDateTime.now()).build(),
        HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = ResponseStatusException.class)
  public ResponseEntity<Object> responseStatusExceptionHandler(ResponseStatusException ex) {
    return new ResponseEntity<>(
        ExceptionResponse.builder().error(ex.getReason()).time(LocalDateTime.now()).build(),
        ex.getStatusCode());
  }
}
