package com.omenaapp.auth_service.adapter.out.security;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.omenaapp.auth_service.domain.port.out.PasswordHasherPort;

@Component
public class BCryptPasswordHasher implements PasswordHasherPort {
  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Override
  public String hash(String rawPassword) {
    return encoder.encode(rawPassword);
  }

  @Override
  public boolean matches(String rawPassword, String hashedPassword) {
    return encoder.matches(rawPassword, hashedPassword);
  }
}
