package com.omenaapp.auth_service.application.port.in;

import java.util.UUID;

public interface LoginUseCase {
  UUID login(Command cmd);
  record Command(String identifier, String password) {} // email ou username
}
