package com.omenaapp.auth_service.application.port.in;

public interface LoginUseCase {
  Result login(Command cmd);

  record Command(String identifier, String password) {}
  record Result(String accessToken) {}
}