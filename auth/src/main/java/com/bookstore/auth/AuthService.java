package com.bookstore.auth;

import com.bookstore.auth.dto.AuthResponseDto;
import com.bookstore.auth.util.JwtUtil;
import com.bookstore.clients.notification.NotificationRequestDto;
import com.bookstore.clients.user.UserClient;
import com.bookstore.clients.user.dto.LoginRequestDto;
import com.bookstore.clients.user.dto.SignupRequestDto;
import com.bookstore.clients.user.dto.SignupResponseDto;
import com.bookstore.clients.user.dto.ValidatedUserDto;
import com.bookstore.kafka.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtUtil jwtUtil;
  private final UserClient userClient;
  private final KafkaProducer kafkaProducer;

  public AuthResponseDto signup(SignupRequestDto signupRequestDto) {
    SignupResponseDto signupResponseDto = userClient.createUser(signupRequestDto);
    kafkaProducer.sendMessage(
        "notification",
        NotificationRequestDto.builder()
            .toUserId(signupResponseDto.getUserId())
            .toUserFirstName(signupResponseDto.getFirstName())
            .toUserEmail(signupResponseDto.getEmail())
            .token(signupResponseDto.getUserConfirmationToken())
            .build());
    return AuthResponseDto.builder().token(signupResponseDto.getUserConfirmationToken()).build();
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
