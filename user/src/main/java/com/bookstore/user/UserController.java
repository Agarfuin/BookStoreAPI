package com.bookstore.user;

import com.bookstore.user.dto.CreateUserRequestDto;
import com.bookstore.user.dto.CreateUserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/hello-world")
  public String helloWorld() {
    return "Hello World";
  }

  @PostMapping("/auth/new")
  public ResponseEntity<CreateUserResponseDto> createUser(
      @RequestBody CreateUserRequestDto createUserRequestDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.createUser(createUserRequestDto));
  }
}
