package com.bookstore.notification;

import com.bookstore.clients.notification.NotificationRequestDto;
import com.bookstore.notification.entity.NotificationEntity;
import com.bookstore.notification.repository.NotificationRepository;
import com.bookstore.notification.util.EmailUtil;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationService {

  @Value("${spring.mail.fromMail}")
  private String fromMail;

  private final JavaMailSender mailSender;
  private final EmailUtil emailUtil;
  private final NotificationRepository notificationRepository;

  public void handleNotification(NotificationRequestDto notificationRequestDto) {
    if (notificationRepository.findByToUserId(notificationRequestDto.getToUserId()).isPresent()) {
      return;
    }
    notificationRepository.save(
        NotificationEntity.builder()
            .toUserId(notificationRequestDto.getToUserId())
            .toUserEmail(notificationRequestDto.getToUserEmail())
            .toUserFirstName(notificationRequestDto.getToUserFirstName())
            .build());

    sendHTMLEmail(
        notificationRequestDto.getToUserEmail(),
        notificationRequestDto.getToUserFirstName(),
        notificationRequestDto.getToken());
  }

  private void sendHTMLEmail(String toMail, String firstName, String token)
      throws ResponseStatusException {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(new InternetAddress(fromMail));
      helper.setReplyTo(fromMail);
      helper.setTo(toMail);
      helper.setSubject("Verify your email!");
      helper.setText(emailUtil.getHtmlBody(firstName, token), true);

      mailSender.send(message);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}
