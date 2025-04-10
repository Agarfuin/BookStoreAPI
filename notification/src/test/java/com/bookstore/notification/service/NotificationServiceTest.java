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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock private JavaMailSender mailSender;

  @Mock private NotificationRepository notificationRepository;

  @InjectMocks private NotificationService notificationService;

  private NotificationRequestDto notificationRequestDto;
  private static UUID toUserId;

  @BeforeEach
  void setUp() {
    toUserId = UUID.fromString("bad60fa3-869b-4b27-8a2d-fff421002ff0"); // Test To User ID

    notificationRequestDto =
        NotificationRequestDto.builder()
            .toUserId(toUserId)
            .toUserEmail("test@example.com")
            .toUserFirstName("John")
            .subject("Test Subject")
            .token("testToken")
            .build();
  }

  @Test
  void handleNotification_WhenNotificationAlreadyExists_ShouldSkipSavingAndSending() {
    when(notificationRepository.findByToUserId(toUserId))
        .thenReturn(Optional.of(new NotificationEntity()));

    notificationService.handleNotification(notificationRequestDto);

    verify(notificationRepository, never()).save(any());
    verify(mailSender, never()).send(any(MimeMessage.class));
  }

  @Test
  void handleNotification_ShouldPersistNotificationAndSendEmail() throws Exception {
    when(notificationRepository.findByToUserId(toUserId)).thenReturn(Optional.empty());
    when(mailSender.createMimeMessage()).thenReturn(mock(MimeMessage.class));

    notificationService.handleNotification(notificationRequestDto);

    ArgumentCaptor<NotificationEntity> notificationEntityCaptor =
        ArgumentCaptor.forClass(NotificationEntity.class);
    verify(notificationRepository, times(1)).save(notificationEntityCaptor.capture());
    verify(mailSender, times(1)).send(any(MimeMessage.class));

    NotificationEntity savedEntity = notificationEntityCaptor.getValue();
    assertNotNull(savedEntity);
    assertEquals(notificationRequestDto.getToUserId(), savedEntity.getToUserId());
    assertEquals(notificationRequestDto.getToUserEmail(), savedEntity.getToUserEmail());
    assertEquals(notificationRequestDto.getToUserFirstName(), savedEntity.getToUserFirstName());
  }

  @Test
  void handleNotification_WhenEmailDeliveryFails_ShouldThrowInternalServerError() throws Exception {
    when(notificationRepository.findByToUserId(toUserId)).thenReturn(Optional.empty());
    MimeMessage mimeMessage = mock(MimeMessage.class);
    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    doThrow(new RuntimeException("Email sending failed"))
        .when(mailSender)
        .send(any(MimeMessage.class));

    ResponseStatusException exception =
        assertThrows(
            ResponseStatusException.class,
            () -> notificationService.handleNotification(notificationRequestDto));

    assertEquals("500 INTERNAL_SERVER_ERROR \"Email sending failed\"", exception.getMessage());
  }
}
