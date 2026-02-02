package com.omenaapp.auth_service.application.port.in;

public interface RefreshUseCase {
  Result refresh(Command cmd);
  record Command(String refreshToken) {}
  record Result(String accessToken, String refreshToken) {}
}
