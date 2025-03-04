package com.bookstore.clients.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user")
public interface UserClient {

  @PostMapping("/api/v1/user/new")
  SignupResponseDto createUser(@RequestBody SignupRequestDto signupRequestDto);

  @PostMapping("/api/v1/user/validate")
  ValidatedUserDto validateCredentials(@RequestBody LoginRequestDto loginRequestDto);

  @PatchMapping("/api/v1/user/verify")
  void verifyUser(@RequestParam("token") String token);
}
