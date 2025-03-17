package com.bookstore.kafka.producer;

import com.bookstore.clients.notification.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

  private final KafkaTemplate<String, NotificationRequestDto> kafkaTemplate;

  public void sendMessage(String topic, NotificationRequestDto payload) {
    kafkaTemplate.send(topic, payload);
  }
}
