package com.bookstore.apigateway.filter;

import com.bookstore.apigateway.config.SecurityConfig;
import com.bookstore.apigateway.util.JwtUtil;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter {

  private final JwtUtil jwtUtil;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String path = exchange.getRequest().getPath().value();
    if (SecurityConfig.WHITELISTED_URLS.stream()
        .anyMatch(pattern -> antPathMatcher.match(pattern, path))) {
      return chain.filter(exchange);
    }

    String token = jwtUtil.extractToken(exchange.getRequest());
    if (token == null || !jwtUtil.validateToken(token)) {
      return unauthorized(exchange, "Invalid or missing token");
    }

    String username = jwtUtil.getUsernameFromToken(token);
    String role = jwtUtil.getRoleFromToken(token);
    if (username == null || role == null) {
      return unauthorized(exchange, "Invalid token");
    }

    List<SimpleGrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(username, null, authorities);

    SecurityContext context = new SecurityContextImpl(authentication);

    return chain
        .filter(exchange)
        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
  }

  private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    String body = String.format("{\"error\":\"Unauthorized\", \"message\":\"%s\"}", message);
    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());
    return exchange.getResponse().writeWith(Mono.just(buffer));
  }
}
