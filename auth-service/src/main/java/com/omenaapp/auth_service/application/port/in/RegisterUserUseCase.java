package com.omenaapp.auth_service.application.port.in;

import java.util.UUID;

public interface RegisterUserUseCase {
  UUID register(Command cmd);
  record Command(String email, String username, String password) {}
}