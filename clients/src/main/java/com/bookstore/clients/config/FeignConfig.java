package com.bookstore.clients.config;

import com.bookstore.clients.exception.ClientExceptionErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
  @Bean
  public ErrorDecoder errorDecoder() {
    return new ClientExceptionErrorDecoder();
  }
}
