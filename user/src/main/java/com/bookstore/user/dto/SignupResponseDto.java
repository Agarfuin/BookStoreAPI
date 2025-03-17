package com.bookstore.user.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupResponseDto {

  private UUID userId;
  private String firstName;
  private String email;
  private String userConfirmationToken;
}
