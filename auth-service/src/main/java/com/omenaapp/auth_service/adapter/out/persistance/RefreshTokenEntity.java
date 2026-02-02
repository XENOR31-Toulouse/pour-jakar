package com.omenaapp.auth_service.adapter.out.persistance;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name="ix_refresh_tokens_hash", columnList = "tokenHash")
})
public class RefreshTokenEntity {
  @Id
  private UUID id;

  @Column(nullable=false)
  private UUID userId;

  @Column(nullable=false, length=64)
  private String tokenHash;

  @Column(nullable=false)
  private Instant expiresAt;

  private Instant revokedAt;

  @Column(nullable=false)
  private Instant createdAt;

  protected RefreshTokenEntity() {}

  public RefreshTokenEntity(UUID id, UUID userId, String tokenHash, Instant expiresAt, Instant revokedAt, Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
    this.revokedAt = revokedAt;
    this.createdAt = createdAt;
  }

  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public String getTokenHash() { return tokenHash; }
  public Instant getExpiresAt() { return expiresAt; }
  public Instant getRevokedAt() { return revokedAt; }
  public Instant getCreatedAt() { return createdAt; }

  public void setRevokedAt(Instant revokedAt) { this.revokedAt = revokedAt; }
}
