package com.bookstore.clients.config;

import com.bookstore.clients.exception.ClientExceptionErrorDecoder;
import feign.Client;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
  @Bean
  public ErrorDecoder errorDecoder() {
    return new ClientExceptionErrorDecoder();
  }

  @Bean
  public Client feignClient() {
    return new ApacheHttpClient();
  }
}
