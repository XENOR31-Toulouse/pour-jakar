package com.omenaapp.auth_service.adapter.out.persistance;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


@Entity
@Table(name = "password_reset_tokens", indexes = {
    @Index(name="ix_prt_token_hash", columnList = "tokenHash"),
    @Index(name="ix_prt_user_id", columnList = "userId")
})
public class PasswordResetTokenEntity {
  @Id
  private UUID id;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false, length = 64)
  private String tokenHash;

  @Column(nullable = false)
  private Instant expiresAt;

  private Instant usedAt;

  @Column(nullable = false)
  private Instant createdAt;

  protected PasswordResetTokenEntity() {}

  public PasswordResetTokenEntity(UUID id, UUID userId, String tokenHash, Instant expiresAt, Instant usedAt, Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
    this.usedAt = usedAt;
    this.createdAt = createdAt;
  }

  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public String getTokenHash() { return tokenHash; }
  public Instant getExpiresAt() { return expiresAt; }
  public Instant getUsedAt() { return usedAt; }
  public Instant getCreatedAt() { return createdAt; }

  public void setUsedAt(Instant usedAt) { this.usedAt = usedAt; }
}
