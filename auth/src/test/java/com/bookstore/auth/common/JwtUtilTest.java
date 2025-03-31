package com.bookstore.auth.common;

import static org.junit.jupiter.api.Assertions.*;

import com.bookstore.auth.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

  private JwtUtil jwtUtil;
  private static final String SECRET_KEY =
      "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"; // Mock Jwt Secret Key

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(jwtUtil, "secretKey", SECRET_KEY);
  }

  @Test
  void generateToken_ShouldGenerateValidToken() {
    String email = "test@example.com";
    String role = "USER";

    String token = jwtUtil.generateToken(email, role);

    Claims claims =
        Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY.getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody();

    assertNotNull(token);
    assertEquals(email, claims.getSubject());
    assertEquals(role, claims.get("role"));
  }
}
