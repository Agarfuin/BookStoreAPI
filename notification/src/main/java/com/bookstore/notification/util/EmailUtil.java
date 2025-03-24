package com.bookstore.notification.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

public class EmailUtil {

  private static final String VERIFICATION_ENDPOINT =
      "http://localhost:8080/api/v1/auth/verify?token=";
  private static final String VERIFICATION_MAIL_HTML = "html/verificationEmail.html";

  public static String getHtmlBody(String firstName, String token) throws IOException {
    ClassPathResource resource = new ClassPathResource(VERIFICATION_MAIL_HTML);

    String content = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

    content =
        content
            .replace("{{first_name}}", firstName)
            .replace("{{verification_link}}", getVerificationLink(token));

    return content;
  }

  private static String getVerificationLink(String token) {
    return VERIFICATION_ENDPOINT + token;
  }
}
