package com.bookstore.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bookstore.user.UserService;
import com.bookstore.user.dto.SignupRequestDto;
import com.bookstore.user.dto.SignupResponseDto;
import com.bookstore.user.dto.ValidatedUserDto;
import com.bookstore.user.entity.UserConfirmationEntity;
import com.bookstore.user.entity.UserEntity;
import com.bookstore.user.enums.Role;
import com.bookstore.user.repository.UserConfirmationRepository;
import com.bookstore.user.repository.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private UserConfirmationRepository userConfirmationRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  private SignupRequestDto signupRequestDto;
  private UserEntity userEntity;
  private UserConfirmationEntity userConfirmationEntity;

  @BeforeEach
  void setUp() {
    signupRequestDto = new SignupRequestDto("John", "Doe", "john.doe@example.com", "password123");
    userEntity =
        UserEntity.builder()
            .id(UUID.fromString("d0e2b1c8-41be-4e5b-9743-2ab706410032")) // Test User ID
            .firstName("John")
            .lastName("Doe")
            .email("john.doe@example.com")
            .password("encodedPassword")
            .role(Role.USER)
            .isValidated(false)
            .createdAt(Instant.now())
            .build();
    userConfirmationEntity =
        UserConfirmationEntity.builder()
            .id(1L)
            .token(userEntity.getId().toString())
            .expirationDate(Date.from(Instant.now().plus(24, ChronoUnit.HOURS)))
            .build();
  }

  @Test
  void createUser_ShouldCreateUserAndConfirmation() {
    when(userRepository.findByEmail(signupRequestDto.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(signupRequestDto.getPassword())).thenReturn("encodedPassword");
    when(userRepository.saveAndFlush(any(UserEntity.class))).thenReturn(userEntity);
    when(userConfirmationRepository.saveAndFlush(any(UserConfirmationEntity.class)))
        .thenReturn(userConfirmationEntity);

    SignupResponseDto response = userService.createUser(signupRequestDto);

    assertNotNull(response);
    assertEquals(userEntity.getEmail(), response.getEmail());

    ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).saveAndFlush(userEntityCaptor.capture());
    assertEquals(userEntity.getEmail(), userEntityCaptor.getValue().getEmail());
    assertEquals(userEntity.getFirstName(), userEntityCaptor.getValue().getFirstName());
    assertEquals(userEntity.getLastName(), userEntityCaptor.getValue().getLastName());
    assertEquals(Boolean.FALSE, userEntityCaptor.getValue().getIsValidated());

    ArgumentCaptor<UserConfirmationEntity> userConfirmationCaptor =
        ArgumentCaptor.forClass(UserConfirmationEntity.class);
    verify(userConfirmationRepository).saveAndFlush(userConfirmationCaptor.capture());
  }

  @Test
  void createUser_WhenEmailExists_ShouldThrowBadRequest() {
    when(userRepository.findByEmail(signupRequestDto.getEmail()))
        .thenReturn(Optional.of(userEntity));

    ResponseStatusException exception =
        assertThrows(ResponseStatusException.class, () -> userService.createUser(signupRequestDto));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("User with provided email already exists", exception.getReason());
  }

  @Test
  void validateCredentials_ShouldReturnValidatedUser() {
    when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));
    when(passwordEncoder.matches("password123", userEntity.getPassword())).thenReturn(true);
    userEntity.setIsValidated(true);

    ValidatedUserDto validatedUser =
        userService.validateCredentials(userEntity.getEmail(), "password123");

    assertNotNull(validatedUser);
    assertEquals(userEntity.getEmail(), validatedUser.getEmail());
    assertEquals(userEntity.getRole().toString(), validatedUser.getRole());
  }

  @Test
  void validateCredentials_WhenInvalidPassword_ShouldThrowUnauthorized() {
    when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));
    when(passwordEncoder.matches("wrongPassword", userEntity.getPassword())).thenReturn(false);

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> userService.validateCredentials(userEntity.getEmail(), "wrongPassword"));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Invalid password", exception.getReason());
  }

  @Test
  void validateCredentials_WhenUserNotValidated_ShouldThrowUnauthorized() {
    when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));
    when(passwordEncoder.matches("password123", userEntity.getPassword())).thenReturn(true);

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> userService.validateCredentials(userEntity.getEmail(), "password123"));
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("User not validated. Please verify your email to login", exception.getReason());
  }

  @Test
  void verifyUser_ShouldMarkUserAsValidated() {
    when(userConfirmationRepository.findByToken(userConfirmationEntity.getToken()))
        .thenReturn(Optional.of(userConfirmationEntity));
    when(userRepository.findByConfirmationToken(userConfirmationEntity.getToken()))
        .thenReturn(Optional.of(userEntity));

    userService.verifyUser(userConfirmationEntity.getToken());

    assertTrue(userEntity.getIsValidated());
    verify(userRepository).save(userEntity);
  }

  @Test
  void verifyUser_WhenTokenExpired_ShouldThrowBadRequest() {
    userConfirmationEntity.setExpirationDate(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)));
    when(userConfirmationRepository.findByToken(userConfirmationEntity.getToken()))
        .thenReturn(Optional.of(userConfirmationEntity));
    when(userRepository.findByConfirmationToken(userConfirmationEntity.getToken()))
        .thenReturn(Optional.of(userEntity));

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> userService.verifyUser(userConfirmationEntity.getToken()));
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Token expired", exception.getReason());
  }

  @Test
  void getUserDetails_ShouldReturnUserDetails() {
    when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));

    ValidatedUserDto userDetails = userService.getUserDetails(userEntity.getEmail());

    assertNotNull(userDetails);
    assertEquals(userEntity.getEmail(), userDetails.getEmail());
    assertEquals(userEntity.getFirstName(), userDetails.getFirstName());
    assertEquals(userEntity.getLastName(), userDetails.getLastName());
  }

  @Test
  void getUserDetails_WhenUserNotFound_ShouldThrowInternalServerError() {
    when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.empty());

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class, () -> userService.getUserDetails(userEntity.getEmail()));
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
    assertEquals(
        String.format("No user found with email: %s", userEntity.getEmail()),
        exception.getReason());
  }
}
