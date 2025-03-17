package com.bookstore.notification.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
public class EmailUtil {

  private final String VERIFICATION_ENDPOINT = "http://localhost:8080/api/v1/auth/verify?token=";

  public String getHtmlBody(String firstName, String token) throws IOException {
    ClassPathResource resource = new ClassPathResource("html/verificationEmail.html");

    String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

    content =
        content
            .replace("{{first_name}}", firstName)
            .replace("{{verification_link}}", getVerificationLink(token));

    return content;
  }

  private String getVerificationLink(String token) {
    return VERIFICATION_ENDPOINT + token;
  }
}
