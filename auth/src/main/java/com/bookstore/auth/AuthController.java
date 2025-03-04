package com.bookstore.auth;

import com.bookstore.auth.dto.AuthResponseDto;
import com.bookstore.clients.user.LoginRequestDto;
import com.bookstore.clients.user.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @GetMapping("/hello-world")
  @ResponseStatus(HttpStatus.OK)
  public String helloWorld() {
    return "Hello World";
  }

  @PostMapping("/signup")
  public ResponseEntity<AuthResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
    // TODO: CREATE VERIFICATION SERVICE AND SEND VERIFICATION EMAIL
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signupRequestDto));
  }

  @GetMapping("/verify")
  public ResponseEntity<String> verifyEmail(@RequestParam String token) {
    authService.verifyEmail(token);
    return ResponseEntity.status(HttpStatus.OK).body("Email verified!");
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
    return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequestDto));
  }
}
