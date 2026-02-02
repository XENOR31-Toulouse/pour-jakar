package com.omenaapp.auth_service.application.port.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


public interface PasswordResetTokenRepositoryPort {

  void save(ResetTokenRecord token);

  Optional<ResetTokenRecord> findValidByTokenHash(String tokenHash, Instant now);

  void markUsed(UUID tokenId, Instant now);

  record ResetTokenRecord(
      UUID id,
      UUID userId,
      String tokenHash,
      Instant expiresAt,
      Instant usedAt,
      Instant createdAt
  ) {}
}
