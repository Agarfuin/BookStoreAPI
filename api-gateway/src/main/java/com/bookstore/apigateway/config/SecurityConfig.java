package com.bookstore.apigateway.config;

import com.bookstore.apigateway.filter.JwtFilter;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  public static final List<String> WHITELISTED_URLS = List.of("/api/v1/auth/**");

  public static final Map<String, String> AUTHORIZED_URLS =
      Map.of("/api/v1/users/**", "USER", "/api/v1/books/**", "USER", "/api/v1/admin/**", "ADMIN");

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http, JwtFilter jwtFilter) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(
            exchanges -> {
              WHITELISTED_URLS.forEach(url -> exchanges.pathMatchers(url).permitAll());
              AUTHORIZED_URLS.forEach(
                  (url, role) -> exchanges.pathMatchers(url).hasAnyRole(role, "ADMIN"));
              exchanges.anyExchange().hasRole("ADMIN");
            })
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        .build();
  }
}
