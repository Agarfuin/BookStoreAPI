package com.bookstore.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bookstore.clients.notification.NotificationRequestDto;
import com.bookstore.notification.NotificationService;
import com.bookstore.notification.entity.NotificationEntity;
import com.bookstore.notification.repository.NotificationRepository;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private JavaMailSender mailSender;

  @Mock private NotificationRepository notificationRepository;

  @InjectMocks private NotificationService notificationService;

  private NotificationRequestDto notificationRequestDto;

  @BeforeEach
  void setUp() {
    notificationRequestDto =
        NotificationRequestDto.builder()
            .toUserId(UUID.randomUUID())
            .toUserEmail("test@example.com")
            .toUserFirstName("John")
            .subject("Test Subject")
            .token("testToken")
            .build();

    ReflectionTestUtils.setField(notificationService, "fromMail", "noreply@example.com");
  }

  @Test
  void handleNotification_WhenNotificationAlreadyExists_ShouldSkipSavingAndSending() {
    UUID userId = UUID.randomUUID();
    when(notificationRepository.findByToUserId(userId))
        .thenReturn(Optional.of(new NotificationEntity()));

    notificationRequestDto.setToUserId(userId);
    notificationService.handleNotification(notificationRequestDto);

    verify(notificationRepository, never()).save(any());
    verify(mailSender, never()).send(any(MimeMessage.class));
  }

  @Test
  void handleNotification_ShouldPersistNotificationAndSendEmail() throws Exception {
    UUID userId = UUID.randomUUID();
    when(notificationRepository.findByToUserId(userId)).thenReturn(Optional.empty());
    when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

    notificationRequestDto.setToUserId(userId);
    notificationService.handleNotification(notificationRequestDto);

    verify(notificationRepository, times(1)).save(any(NotificationEntity.class));
    verify(mailSender, times(1)).send(any(MimeMessage.class));
  }

  @Test
  void handleNotification_WhenEmailDeliveryFails_ShouldThrowInternalServerError() throws Exception {
    UUID userId = UUID.randomUUID();
    when(notificationRepository.findByToUserId(userId)).thenReturn(Optional.empty());
    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new RuntimeException("Email sending failed"))
        .when(mailSender)
        .send(any(MimeMessage.class));

    notificationRequestDto.setToUserId(userId);
    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> notificationService.handleNotification(notificationRequestDto));

    assertEquals("500 INTERNAL_SERVER_ERROR \"Email sending failed\"", exception.getMessage());
  }
}
