package com.bookstore.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

  @Value("${application.security.jwt.secret-key}")
  private String secretKey;

  public String extractToken(ServerHttpRequest request) {
    String header = request.getHeaders().getFirst("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }

  private Claims extractClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public Boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  public String getUsernameFromToken(String token) {
    return extractClaims(token).getSubject();
  }

  public String getRoleFromToken(String token) {
    return extractClaims(token).get("role", String.class);
  }

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }
}
