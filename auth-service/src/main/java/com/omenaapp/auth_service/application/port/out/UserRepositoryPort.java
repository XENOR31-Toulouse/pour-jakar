package com.omenaapp.auth_service.application.port.out;

import java.util.Optional;

import com.omenaapp.auth_service.domain.User;

public interface UserRepositoryPort {
  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
  void save(User user);
}
