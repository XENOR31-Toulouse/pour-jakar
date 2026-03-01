package com.omenaapp.auth_service.adapter.out.security;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.omenaapp.auth_service.domain.port.out.PasswordResetNotifierPort;


@Component
public class SmtpPasswordResetNotifier implements PasswordResetNotifierPort {

  private final JavaMailSender mailSender;
  private final String from;
  private final String frontBaseUrl;

  public SmtpPasswordResetNotifier(
      JavaMailSender mailSender,
      @Value("${app.mail.from}") String from,
      @Value("${app.front.base-url}") String frontBaseUrl
  ) {
    this.mailSender = mailSender;
    this.from = from;
    this.frontBaseUrl = frontBaseUrl;
  }

  @Override
  public void sendResetLink(String email, String resetToken) {
    String link = frontBaseUrl + "/reset-password?token=" +
        URLEncoder.encode(resetToken, StandardCharsets.UTF_8);


    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(from);
    msg.setTo(email);
    msg.setSubject("Reset password");
    msg.setText("Clique ici pour réinitialiser ton mot de passe:\n" + link);

    mailSender.send(msg);
  }
}

