package com.bookstore.user;

import com.bookstore.user.dto.LoginRequestDto;
import com.bookstore.user.dto.SignupRequestDto;
import com.bookstore.user.dto.SignupResponseDto;
import com.bookstore.user.dto.ValidatedUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/hello-world")
  @ResponseStatus(HttpStatus.OK)
  public String helloWorld() {
    return "Hello World";
  }

  @PostMapping("/new")
  public ResponseEntity<SignupResponseDto> createUser(
      @RequestBody SignupRequestDto signupRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(signupRequestDto));
  }

  @PostMapping("/validate")
  public ResponseEntity<ValidatedUserDto> validateCredentials(
      @RequestBody LoginRequestDto loginRequestDto) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            userService.validateCredentials(
                loginRequestDto.getEmail(), loginRequestDto.getPassword()));
  }

  @PostMapping("/verify")
  @ResponseStatus(HttpStatus.OK)
  public void verifyUser(@RequestParam("token") String token) {
    userService.verifyUser(token);
  }
}
