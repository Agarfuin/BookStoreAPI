package com.bookstore.clients.user;

import com.bookstore.clients.config.FeignConfig;
import com.bookstore.clients.user.dto.LoginRequestDto;
import com.bookstore.clients.user.dto.SignupRequestDto;
import com.bookstore.clients.user.dto.SignupResponseDto;
import com.bookstore.clients.user.dto.ValidatedUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user", url = "${clients.user.url}", configuration = FeignConfig.class)
public interface UserClient {

  @PostMapping("/api/v1/user/new")
  SignupResponseDto createUser(@RequestBody SignupRequestDto signupRequestDto);

  @PostMapping("/api/v1/user/validate")
  ValidatedUserDto validateCredentials(@RequestBody LoginRequestDto loginRequestDto);

  @PostMapping("/api/v1/user/verify")
  void verifyUser(@RequestParam("token") String token);
}
