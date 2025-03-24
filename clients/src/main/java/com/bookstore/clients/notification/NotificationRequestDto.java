package com.bookstore.clients.notification;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {

  private UUID toUserId;
  private String subject;
  private String toUserFirstName;
  private String toUserEmail;
  private String token;
}
