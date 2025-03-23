package com.bookstore.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  private static final Long EXPIRATION = 86400000L; // 1 day

  public String generateToken(String email, String role) {
    return Jwts.builder()
        .setSubject(email)
        .claim("role", role)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
        .signWith(getSecretKey())
        .compact();
  }

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }
}
