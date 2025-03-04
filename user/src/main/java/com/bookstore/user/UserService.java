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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

  private static final int CONFIRMATION_EXPIRATION_HOURS = 24;
  private final UserRepository userRepository;
  private final UserConfirmationRepository userConfirmationRepository;
  private final PasswordEncoder passwordEncoder;

  public SignupResponseDto createUser(SignupRequestDto signupRequestDto) {
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
    userConfirmationRepository.save(userConfirmation);

    return SignupResponseDto.builder().id(userEntity.getId()).email(userEntity.getEmail()).build();
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
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
    }

    return ValidatedUserDto.builder()
        .email(userEntity.getEmail())
        .role(userEntity.getRole().toString())
        .build();
  }

  public void verifyUser(String confirmationToken) {
    UserConfirmationEntity userConfirmationEntity =
        userConfirmationRepository
            .findByToken(confirmationToken)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token"));

    UserEntity user =
        userRepository
            .findByConfirmationToken(confirmationToken)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No user found with provided token"));

    Date currentDate = new Date();
    if (currentDate.after(userConfirmationEntity.getExpirationDate())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token expired");
    }

    user.setIsValidated(true);
    userRepository.save(user);
  }
}
