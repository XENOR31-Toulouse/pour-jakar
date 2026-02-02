package com.omenaapp.auth_service.adapter.out.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.omenaapp.auth_service.application.port.out.PasswordResetNotifierPort;


@Component
public class SmtpPasswordResetNotifier implements PasswordResetNotifierPort {

  private final JavaMailSender mailSender;
  private final String from;

  public SmtpPasswordResetNotifier(JavaMailSender mailSender,
                                  @Value("${app.mail.from}") String from) {
    this.mailSender = mailSender;
    this.from = from;
  }

  @Override
  public void sendResetLink(String email, String resetToken) {
    String link = "http://localhost:4200/reset-password?token=" + resetToken;

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(from);
    msg.setTo(email);
    msg.setSubject("Reset password");
    msg.setText("Click this link to reset your password:\n" + link);

    mailSender.send(msg);
  }
}
