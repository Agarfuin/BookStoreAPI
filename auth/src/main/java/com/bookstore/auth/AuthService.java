package com.bookstore.auth;

import com.bookstore.auth.dto.AuthResponseDto;
import com.bookstore.auth.util.JwtUtil;
import com.bookstore.clients.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtUtil jwtUtil;
  private final UserClient userClient;

  public AuthResponseDto signup(SignupRequestDto signupRequestDto) {
    SignupResponseDto signupResponseDto = userClient.createUser(signupRequestDto);
    return AuthResponseDto.builder()
        .token(jwtUtil.generateToken(signupResponseDto.getEmail(), "USER"))
        .build();
  }

  public AuthResponseDto login(LoginRequestDto loginRequestDto) {
    ValidatedUserDto validatedUser = userClient.validateCredentials(loginRequestDto);

    String token = jwtUtil.generateToken(validatedUser.getEmail(), validatedUser.getRole());
    return AuthResponseDto.builder().token(token).build();
  }

  public void verifyEmail(String token) {
    userClient.verifyUser(token);
  }
}
