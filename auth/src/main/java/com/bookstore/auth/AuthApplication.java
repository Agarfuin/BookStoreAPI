package com.bookstore.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication(
    scanBasePackages = {
      "com.bookstore.auth",
      "com.bookstore.kafka",
    })
@EnableFeignClients(basePackages = {"com.bookstore.clients"})
@PropertySources({@PropertySource("classpath:clients-${spring.profiles.active}.properties")})
public class AuthApplication {
  public static void main(String[] args) {
    SpringApplication.run(AuthApplication.class, args);
  }
}
