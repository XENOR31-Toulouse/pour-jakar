package com.omenaapp.auth_service.adapter.out.persistance;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.omenaapp.auth_service.application.port.out.UserRepositoryPort;
import com.omenaapp.auth_service.domain.User;

@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

  private final SpringDataUserJpaRepository jpa;

  public JpaUserRepositoryAdapter(SpringDataUserJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return jpa.findByEmail(email).map(this::toDomain);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return jpa.findByUsername(username).map(this::toDomain);
  }

  @Override
  public void save(User user) {
    jpa.save(toEntity(user));
  }

  private User toDomain(UserEntity e) {
    return new User(e.getId(), e.getEmail(), e.getUsername(), e.getPasswordHash(), e.getCreatedAt());
  }

  private UserEntity toEntity(User u) {
    return new UserEntity(u.id(), u.email(), u.username(), u.passwordHash(), u.createdAt());
  }
}
