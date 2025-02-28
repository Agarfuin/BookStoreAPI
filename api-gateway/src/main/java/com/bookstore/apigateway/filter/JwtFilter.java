package com.bookstore.apigateway.filter;

import com.bookstore.apigateway.config.JwtConfig;
import com.bookstore.apigateway.config.SecurityConfig;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter implements WebFilter {

  private final JwtConfig jwtConfig;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String path = exchange.getRequest().getPath().value();

    if (SecurityConfig.WHITELISTED_URLS.stream()
        .anyMatch(pattern -> antPathMatcher.match(pattern, path))) {
      return chain.filter(exchange);
    }

    String token = extractToken(exchange.getRequest());

    if (token == null) {
      return unauthorized(exchange, "Token is missing");
    }

    try {
      Jwts.parserBuilder().setSigningKey(jwtConfig.getSecretKey()).build().parseClaimsJws(token);
      return chain.filter(exchange);
    } catch (JwtException e) {
      return unauthorized(exchange, "Token is invalid");
    }
  }

  private String extractToken(ServerHttpRequest request) {
    String header = request.getHeaders().getFirst("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }

  private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    String body = String.format("{\"error\":\"Unauthorized\", \"message\":\"%s\"}", message);
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());
    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}
