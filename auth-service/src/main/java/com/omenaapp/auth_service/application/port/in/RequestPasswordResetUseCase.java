package com.omenaapp.auth_service.application.port.in;

public interface RequestPasswordResetUseCase {
  void request(Command cmd);
  record Command(String email) {}
}
