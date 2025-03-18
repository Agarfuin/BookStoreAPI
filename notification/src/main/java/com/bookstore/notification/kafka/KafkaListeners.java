package com.bookstore.notification.kafka;

import com.bookstore.clients.notification.NotificationRequestDto;
import com.bookstore.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {

  private final NotificationService notificationService;

  @KafkaListener(topics = "notification", groupId = "verificationEmail")
  public void listener(NotificationRequestDto notificationRequestDto) {
    log.info("Received notification request: {}", notificationRequestDto);
    notificationService.handleNotification(notificationRequestDto);
  }
}
