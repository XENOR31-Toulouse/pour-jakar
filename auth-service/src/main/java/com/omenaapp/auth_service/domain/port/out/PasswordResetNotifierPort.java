package com.omenaapp.auth_service.domain.port.out;

public interface PasswordResetNotifierPort {
  void sendResetLink(String email, String resetToken);
}
