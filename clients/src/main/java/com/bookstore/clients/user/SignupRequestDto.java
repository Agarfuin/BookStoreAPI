package com.bookstore.clients.user;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

  @NotNull private String firstName;
  @NotNull private String lastName;
  @NotNull private String email;
  @NotNull private String password;
}
