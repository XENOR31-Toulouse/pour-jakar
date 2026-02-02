package com.omenaapp.auth_service.application.port.out;

public interface PasswordResetNotifierPort {
  void sendResetLink(String email, String resetToken);
}
