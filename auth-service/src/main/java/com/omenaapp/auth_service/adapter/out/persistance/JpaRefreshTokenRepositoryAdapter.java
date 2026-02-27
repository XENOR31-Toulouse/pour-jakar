package com.omenaapp.auth_service.adapter.out.persistance;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.omenaapp.auth_service.domain.port.out.RefreshTokenRepositoryPort;

@Repository
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

  private final SpringDataRefreshTokenJpaRepository jpa;

  public JpaRefreshTokenRepositoryAdapter(SpringDataRefreshTokenJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public void save(RefreshTokenRecord token) {
    jpa.save(new RefreshTokenEntity(
        token.id(), token.userId(), token.tokenHash(),
        token.expiresAt(), token.revokedAt(), token.createdAt()
    ));
  }

  @Override
  public Optional<RefreshTokenRecord> findValidByTokenHash(String tokenHash, Instant now) {
    return jpa.findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(tokenHash, now)
        .map(e -> new RefreshTokenRecord(
            e.getId(), e.getUserId(), e.getTokenHash(),
            e.getExpiresAt(), e.getRevokedAt(), e.getCreatedAt()
        ));
  }

  @Override
  public void revokeToken(UUID tokenId, Instant now) {
    var ent = jpa.findById(tokenId).orElseThrow(() -> new IllegalArgumentException("REFRESH_NOT_FOUND"));
    ent.setRevokedAt(now);
    jpa.save(ent);
  }
}
