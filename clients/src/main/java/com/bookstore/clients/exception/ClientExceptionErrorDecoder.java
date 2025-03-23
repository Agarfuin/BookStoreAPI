package com.bookstore.clients.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
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
  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @Override
  public Exception decode(String methodKey, Response response) {
    String responseBody = getResponseBody(response);
    String errorMessage = extractErrorMessage(responseBody);

    log.info("status code: {}, message: {}", response.status(), errorMessage);
    log.info("body: {}", responseBody);
    return switch (response.status()) {
      case 400 -> new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
      case 401 -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
      case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
      case 500 -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
      default -> defaultErrorDecoder.decode(methodKey, response);
    };
  }

  private String getResponseBody(Response response) {
    try {
      return response.body() != null
          ? new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8)
          : null;
    } catch (IOException e) {
      log.error("Error reading response body", e);
      return null;
    }
  }

  private String extractErrorMessage(String responseBody) {
    if (responseBody == null) return "Unknown error";

    try {
      JsonNode rootNode = objectMapper.readTree(responseBody);
      if (rootNode.has("error")) {
        return rootNode.get("error").asText();
      }
    } catch (IOException e) {
      log.error("Error parsing response body: {}", responseBody, e);
    }

    return responseBody;
  }
}
