package com.omenaapp.auth_service.adapter.out.persistance;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "users",
  uniqueConstraints = {
    @UniqueConstraint(name="uk_users_email", columnNames = "email"),
    @UniqueConstraint(name="uk_users_username", columnNames = "username")
  }
)
public class UserEntity {
  @Id
  private UUID id;

  @Column(nullable=false)
  private String email;

  @Column(nullable=false)
  private String username;

  @Column(nullable=false)
  private String passwordHash;

  @Column(nullable=false)
  private Instant createdAt;

  protected UserEntity() {}

  public UserEntity(UUID id, String email, String username, String passwordHash, Instant createdAt) {
    this.id = id;
    this.email = email;
    this.username = username;
    this.passwordHash = passwordHash;
    this.createdAt = createdAt;
  }

  public UUID getId() { return id; }
  public String getEmail() { return email; }
  public String getUsername() { return username; }
  public String getPasswordHash() { return passwordHash; }
  public Instant getCreatedAt() { return createdAt; }
}
