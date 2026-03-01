package com.omenaapp.auth_service.application.port.in;

public interface ResetPasswordUseCase {
  void reset(Command cmd);
  record Command(String token, String newPassword) {}
}
