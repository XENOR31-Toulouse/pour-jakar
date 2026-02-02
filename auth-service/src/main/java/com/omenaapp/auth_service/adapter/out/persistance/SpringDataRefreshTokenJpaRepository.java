package com.omenaapp.auth_service.adapter.out.persistance;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataRefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {
  Optional<RefreshTokenEntity> findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(String tokenHash, Instant now);
}
