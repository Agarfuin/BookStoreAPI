 package com.bookstore.clients.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
 public class ClientExceptionErrorDecoder implements ErrorDecoder {

  private final ErrorDecoder defaultErrorDecoder = new Default();
  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new
 JavaTimeModule());

  @Override
  public Exception decode(String methodKey, Response response) {
    String responseBody = null;
    try {
      if (response.body() != null) {
        responseBody =
            new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
      }
    } catch (IOException e) {
      log.error("Error reading response body", e);
    }

    String errorMessage = "Unknown error occurred";
    if (responseBody != null) {
      try {
        ExceptionResponse exceptionResponse =
            objectMapper.readValue(responseBody, ExceptionResponse.class);
        errorMessage = exceptionResponse.getError();
      } catch (IOException e) {
        log.error("Error parsing response body", e);
      }
    }

    return switch (response.status()) {
      case 400 -> new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
      case 401 -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
      case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
      case 500 -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
      default -> defaultErrorDecoder.decode(methodKey, response);
    };
  }
 }
