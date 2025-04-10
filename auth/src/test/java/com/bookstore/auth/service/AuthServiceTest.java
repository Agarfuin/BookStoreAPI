package com.bookstore.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bookstore.auth.AuthService;
import com.bookstore.auth.dto.AuthResponseDto;
import com.bookstore.auth.util.JwtUtil;
import com.bookstore.clients.notification.NotificationRequestDto;
import com.bookstore.clients.user.UserClient;
import com.bookstore.clients.user.dto.LoginRequestDto;
import com.bookstore.clients.user.dto.SignupRequestDto;
import com.bookstore.clients.user.dto.SignupResponseDto;
import com.bookstore.clients.user.dto.ValidatedUserDto;
import com.bookstore.kafka.producer.KafkaProducer;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private JwtUtil jwtUtil;

  @Mock private UserClient userClient;

  @Mock private KafkaProducer kafkaProducer;

  @InjectMocks private AuthService authService;

  private SignupRequestDto signupRequest;
  private SignupResponseDto signupResponse;
  private LoginRequestDto loginRequest;
  private ValidatedUserDto validatedUser;

  @BeforeEach
  void setUp() {
    UUID userId = UUID.fromString("d0e2b1c8-41be-4e5b-9743-2ab706410032"); // Test User ID
    signupRequest = new SignupRequestDto("John", "Doe", "john.doe@example.com", "password123");
    signupResponse =
        new SignupResponseDto(userId, "John", "john.doe@example.com", "confirmationToken");
    loginRequest = new LoginRequestDto("john.doe@example.com", "password123");
    validatedUser = new ValidatedUserDto("john.doe@example.com", "USER");
  }

  @Test
  void signup_ShouldReturnConfirmationTokenAndSendNotification() {
    when(userClient.createUser(signupRequest)).thenReturn(signupResponse);
    doNothing()
        .when(kafkaProducer)
        .sendMessage(eq("notification"), any(NotificationRequestDto.class));

    AuthResponseDto response = authService.signup(signupRequest);

    assertNotNull(response);
    assertEquals("confirmationToken", response.getToken());
  }

  @Test
  void login_ShouldReturnJwtToken() {
    when(userClient.validateCredentials(loginRequest)).thenReturn(validatedUser);
    when(jwtUtil.generateToken("john.doe@example.com", "USER")).thenReturn("jwtToken");

    AuthResponseDto response = authService.login(loginRequest);

    assertNotNull(response);
    assertEquals("jwtToken", response.getToken());
  }

  @Test
  void verifyEmail_ShouldNotThrowException() {
    doNothing().when(userClient).verifyUser("confirmationToken");

    assertDoesNotThrow(() -> authService.verifyEmail("confirmationToken"));
  }

  @Test
  void generateAdminToken_ShouldReturnAdminJwtToken() {
    when(jwtUtil.generateToken("agarfuinbookstore@mail.com", "ADMIN")).thenReturn("adminJwtToken");

    AuthResponseDto response = authService.generateAdminToken();

    assertNotNull(response);
    assertEquals("adminJwtToken", response.getToken());
  }
}
