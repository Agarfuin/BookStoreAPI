package com.bookstore.notification;

import com.bookstore.clients.notification.NotificationRequestDto;
import com.bookstore.notification.entity.NotificationEntity;
import com.bookstore.notification.repository.NotificationRepository;
import com.bookstore.notification.util.EmailUtil;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private static final String FROM_MAIL = "noreply@agarfuinbookstore.com";

  private final JavaMailSender mailSender;
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

    try {
      String htmlBody =
          EmailUtil.getHtmlBody(
              notificationRequestDto.getToUserFirstName(), notificationRequestDto.getToken());

      sendHTMLEmail(
          notificationRequestDto.getToUserEmail(), notificationRequestDto.getSubject(), htmlBody);
    } catch (IOException e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void sendHTMLEmail(String toMail, String subject, String htmlContent)
      throws ResponseStatusException {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(new InternetAddress(FROM_MAIL));
      helper.setReplyTo(FROM_MAIL);
      helper.setTo(toMail);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);

      mailSender.send(message);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void sendSimpleEmail(String toMail, String subject, String body)
      throws ResponseStatusException {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(new InternetAddress(FROM_MAIL));
      helper.setReplyTo(FROM_MAIL);
      helper.setTo(toMail);
      helper.setSubject(subject);
      helper.setText(body);

      mailSender.send(message);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }
}
