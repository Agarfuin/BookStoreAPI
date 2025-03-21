package com.bookstore.auth;

import com.bookstore.auth.dto.AuthResponseDto;
import com.bookstore.clients.user.dto.LoginRequestDto;
import com.bookstore.clients.user.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @GetMapping({"/public/hello-world", "/protected/hello-world"})
  @ResponseStatus(HttpStatus.OK)
  public String helloWorld() {
    return "Hello World";
  }

  @PostMapping("/signup")
  public ResponseEntity<AuthResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(signupRequestDto));
  }

  @GetMapping("/verify")
  public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
    authService.verifyEmail(token);
    return ResponseEntity.status(HttpStatus.OK).body("Email verified!");
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
    return ResponseEntity.status(HttpStatus.OK).body(authService.login(loginRequestDto));
  }

  @GetMapping("/generate-admin-token")
  public ResponseEntity<AuthResponseDto> generateAdminToken() {
    return ResponseEntity.status(HttpStatus.OK).body(authService.generateAdminToken());
  }
}
