package com.omenaapp.auth_service.application.port.out;

public interface PasswordHasherPort {
  String hash(String rawPassword);
  boolean matches(String rawPassword, String hashedPassword);
}