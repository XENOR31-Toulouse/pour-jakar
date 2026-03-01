package com.omenaapp.auth_service.application.port.in;

public interface LogoutUseCase {
  void logout(Command cmd);
  record Command(String refreshToken) {}
}
