package com.omenaapp.auth_service.adapter.out.persistance;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.omenaapp.auth_service.domain.port.out.UserRepositoryPort;
import com.omenaapp.auth_service.domain.model.User;

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
  public Optional<User> findById(UUID id) {
    return jpa.findById(id).map(this::toDomain);
  }

  @Override
  public List<User> findEmployees() {
    return jpa.findAll().stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public void deleteById(UUID id) {
    jpa.deleteById(id);
  }

  @Override
  public void save(User user) {
    jpa.save(toEntity(user));
  }

  private User toDomain(UserEntity e) {
    return new User(
        e.getId(),
        e.getEmail(),
        e.getUsername(),
        e.getPasswordHash(),
        e.getCreatedAt(),
        e.isAdmin()
    );
  }

  private UserEntity toEntity(User u) {
    return new UserEntity(
        u.id(),
        u.email(),
        u.username(),
        u.passwordHash(),
        u.createdAt(),
        u.isAdmin()
    );
  }
}