package com.bookstore.auth.config;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  @Bean
  public SecretKey jwtSecretKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }
}
