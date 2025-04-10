package com.bookstore.apigateway.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bookstore.apigateway.filter.JwtFilter;
import com.bookstore.apigateway.util.JwtUtil;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

  @Mock private JwtUtil jwtUtil;

  @Mock private WebFilterChain filterChain;

  @InjectMocks private JwtFilter jwtFilter;

  @Test
  void filter_ShouldAllowWhitelistedUrls() {
    MockServerHttpRequest request = MockServerHttpRequest.get("/api/v1/auth/login").build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    when(filterChain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(jwtFilter.filter(exchange, filterChain)).verifyComplete();

    verify(jwtUtil, never()).validateToken(anyString());
  }

  @Test
  void filter_WhenTokenIsInvalid_ShouldReturnUnauthorized() {
    MockServerHttpRequest request =
        MockServerHttpRequest.get("/api/v1/users/cart")
            .header("Authorization", "Bearer invalid-token")
            .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    when(jwtUtil.extractToken(any())).thenReturn("invalid-token");
    when(jwtUtil.validateToken("invalid-token")).thenReturn(false);

    StepVerifier.create(jwtFilter.filter(exchange, filterChain)).verifyComplete();

    assertEquals(401, Objects.requireNonNull(exchange.getResponse().getStatusCode()).value());
  }

  @Test
  void filter_ShouldProcess() {
    MockServerHttpRequest request =
        MockServerHttpRequest.get("/api/v1/users/cart")
            .header("Authorization", "Bearer valid-token")
            .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    when(jwtUtil.extractToken(any())).thenReturn("valid-token");
    when(jwtUtil.validateToken("valid-token")).thenReturn(true);
    when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("user@example.com");
    when(jwtUtil.getRoleFromToken("valid-token")).thenReturn("USER");
    when(filterChain.filter(any())).thenReturn(Mono.empty());

    StepVerifier.create(jwtFilter.filter(exchange, filterChain)).verifyComplete();

    verify(filterChain).filter(any());
  }
}
