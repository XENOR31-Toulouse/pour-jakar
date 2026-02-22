package com.omenaapp.auth_service.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.omenaapp.auth_service.domain.User;

public interface UserRepositoryPort {
  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
  Optional<User> findById(UUID id);
  List<User> findEmployees();     
  void deleteById(UUID id);
  void save(User user);
}
