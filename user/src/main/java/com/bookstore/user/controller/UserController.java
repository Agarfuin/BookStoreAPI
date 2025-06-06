package com.bookstore.user.controller;

import com.bookstore.user.UserService;
import com.bookstore.user.dto.ValidatedUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/hello-world")
  @ResponseStatus(HttpStatus.OK)
  public String helloWorld() {
    return "Hello World";
  }

  @GetMapping("/user")
  public ResponseEntity<ValidatedUserDto> getUserDetails(
      @RequestHeader("X-User-Email") String email) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.getUserDetails(email));
  }

  @DeleteMapping("/user")
  public ResponseEntity<String> deleteUser(@RequestHeader("X-User-Email") String email) {
    return ResponseEntity.status(HttpStatus.OK).body(userService.deleteAccount(email));
  }
}
