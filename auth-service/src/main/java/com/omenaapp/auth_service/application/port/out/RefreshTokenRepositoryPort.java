package com.omenaapp.auth_service.application.port.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepositoryPort {

  void save(RefreshTokenRecord token);

  Optional<RefreshTokenRecord> findValidByTokenHash(String tokenHash, Instant now);

  void revokeToken(UUID tokenId, Instant now);

  record RefreshTokenRecord(
      UUID id,
      UUID userId,
      String tokenHash,
      Instant expiresAt,
      Instant revokedAt,
      Instant createdAt
  ) {}
}
