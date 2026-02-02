package com.omenaapp.auth_service.domain;

import java.time.Instant;
import java.util.UUID;

public class User {
  private final UUID id;
  private final String email;
  private final String username;
  private final String passwordHash;
  private final Instant createdAt;

  public User(UUID id, String email, String username, String passwordHash, Instant createdAt) {
    this.id = id;
    this.email = email;
    this.username = username;
    this.passwordHash = passwordHash;
    this.createdAt = createdAt;
  }

  public UUID id() { return id; }
  public String email() { return email; }
  public String username() { return username; }
  public String passwordHash() { return passwordHash; }
  public Instant createdAt() { return createdAt; }
}