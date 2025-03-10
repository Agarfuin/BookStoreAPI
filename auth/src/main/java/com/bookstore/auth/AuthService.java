package com.bookstore.auth;

import com.bookstore.auth.dto.AuthResponseDto;
import com.bookstore.auth.util.JwtUtil;
import com.bookstore.clients.user.*;
import com.bookstore.clients.user.dto.LoginRequestDto;
import com.bookstore.clients.user.dto.SignupRequestDto;
import com.bookstore.clients.user.dto.SignupResponseDto;
import com.bookstore.clients.user.dto.ValidatedUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtUtil jwtUtil;
  private final UserClient userClient;

  public AuthResponseDto signup(SignupRequestDto signupRequestDto) {
    SignupResponseDto signupResponseDto = userClient.createUser(signupRequestDto);
    // TODO: CREATE NOTIFICATION SERVICE AND SEND VERIFICATION EMAIL
    return AuthResponseDto.builder().token(signupResponseDto.getUserConfirmation()).build();
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
