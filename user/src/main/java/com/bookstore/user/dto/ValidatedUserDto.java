package com.bookstore.user.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidatedUserDto {

  private UUID id;
  private String email;
  private String role;
  private String firstName;
  private String lastName;
  private Boolean isVerified;
  private Instant accountCreationDate;
}
