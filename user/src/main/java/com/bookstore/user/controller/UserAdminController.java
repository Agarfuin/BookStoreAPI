package com.bookstore.user.controller;

import com.bookstore.user.UserService;
import com.bookstore.user.dto.LoginRequestDto;
import com.bookstore.user.dto.SignupRequestDto;
import com.bookstore.user.dto.SignupResponseDto;
import com.bookstore.user.dto.ValidatedUserDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<SignupResponseDto> createUser(
      @RequestBody @Valid SignupRequestDto signupRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(signupRequestDto));
  }

  @PostMapping("/user/validate")
  public ResponseEntity<ValidatedUserDto> validateCredentials(
      @RequestBody @Valid LoginRequestDto loginRequestDto) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            userService.validateCredentials(
                loginRequestDto.getEmail(), loginRequestDto.getPassword()));
  }

  @PostMapping("/user/verify")
  @ResponseStatus(HttpStatus.OK)
  public void verifyUser(@RequestParam("token") String token) {
    userService.verifyUser(token);
  }

  @GetMapping
  public ResponseEntity<List<ValidatedUserDto>> getAllUsers() {
    return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<ValidatedUserDto> getUserDetails(@PathVariable("userId") UUID userId) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
  }
}
