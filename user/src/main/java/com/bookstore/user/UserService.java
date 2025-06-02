package com.bookstore.user;

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
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

  private static final int CONFIRMATION_EXPIRATION_HOURS = 24; // Just for convention, no real usage
  private final UserRepository userRepository;
  private final UserConfirmationRepository userConfirmationRepository;
  private final PasswordEncoder passwordEncoder;

  public SignupResponseDto createUser(SignupRequestDto signupRequestDto) {
    if (userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "User with provided email already exists");
    }

    UserEntity userEntity =
        UserEntity.builder()
            .firstName(signupRequestDto.getFirstName())
            .lastName(signupRequestDto.getLastName())
            .email(signupRequestDto.getEmail())
            .password(passwordEncoder.encode(signupRequestDto.getPassword()))
            .role(Role.USER)
            .build();

    userRepository.saveAndFlush(userEntity);

    UserConfirmationEntity userConfirmation =
        UserConfirmationEntity.builder()
            .token(UUID.randomUUID().toString())
            .userId(userEntity.getId())
            .expirationDate(
                Date.from(Instant.now().plus(CONFIRMATION_EXPIRATION_HOURS, ChronoUnit.HOURS)))
            .build();
    userConfirmationRepository.saveAndFlush(userConfirmation);

    return SignupResponseDto.builder()
        .userId(userEntity.getId())
        .firstName(userEntity.getFirstName())
        .email(userEntity.getEmail())
        .userConfirmationToken(userConfirmation.getToken())
        .build();
  }

  public ValidatedUserDto validateCredentials(String email, String password) {
    UserEntity userEntity =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No user found with provided email"));

    if (!passwordEncoder.matches(password, userEntity.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
    }

    if (!userEntity.getIsValidated()) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "User not validated. Please verify your email to login");
    }

    return ValidatedUserDto.builder()
        .email(userEntity.getEmail())
        .role(userEntity.getRole().toString())
        .build();
  }

  public void verifyUser(String token) {
    UserConfirmationEntity userConfirmationEntity =
        userConfirmationRepository
            .findByToken(token)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token"));

    UserEntity user =
        userRepository
            .findByConfirmationToken(token)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No user found with provided token"));

    Date currentDate = new Date();
    if (currentDate.after(userConfirmationEntity.getExpirationDate())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");
    }

    if (user.getIsValidated()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already verified");
    }

    user.setIsValidated(Boolean.TRUE);
    userRepository.save(user);
  }

  public ValidatedUserDto getUserDetails(String email) {
    UserEntity currentUser =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        String.format("No user found with email: %s", email)));

    return ValidatedUserDto.builder()
        .id(currentUser.getId())
        .email(currentUser.getEmail())
        .firstName(currentUser.getFirstName())
        .lastName(currentUser.getLastName())
        .role(currentUser.getRole().name())
        .isVerified(currentUser.getIsValidated())
        .accountCreationDate(currentUser.getCreatedAt())
        .build();
  }

  public String deleteAccount(String email) {
    UserEntity currentUser =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        String.format("No user found with email: %s", email)));

    userRepository.delete(currentUser);

    return String.format("User with id %s deleted", currentUser.getId());
  }

  public List<ValidatedUserDto> getAllUsers() {
    return userRepository.findAll().stream()
        .map(
            user ->
                ValidatedUserDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole().name())
                    .isVerified(user.getIsValidated())
                    .accountCreationDate(user.getCreatedAt())
                    .build())
        .toList();
  }

  public ValidatedUserDto getUserById(UUID userId) {
    UserEntity userEntity =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, String.format("No user found with id: %s", userId)));

    return ValidatedUserDto.builder()
        .id(userEntity.getId())
        .email(userEntity.getEmail())
        .firstName(userEntity.getFirstName())
        .lastName(userEntity.getLastName())
        .role(userEntity.getRole().name())
        .isVerified(userEntity.getIsValidated())
        .accountCreationDate(userEntity.getCreatedAt())
        .build();
  }
}
