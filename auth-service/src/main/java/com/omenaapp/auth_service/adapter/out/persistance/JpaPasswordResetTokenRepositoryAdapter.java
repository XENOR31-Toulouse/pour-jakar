package com.omenaapp.auth_service.adapter.out.persistance;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.omenaapp.auth_service.domain.port.out.PasswordResetTokenRepositoryPort;

@Repository
public class JpaPasswordResetTokenRepositoryAdapter implements PasswordResetTokenRepositoryPort {

  private final SpringDataPasswordResetTokenJpaRepository jpa;

  public JpaPasswordResetTokenRepositoryAdapter(SpringDataPasswordResetTokenJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public void save(ResetTokenRecord token) {
    jpa.save(new PasswordResetTokenEntity(
        token.id(), token.userId(), token.tokenHash(),
        token.expiresAt(), token.usedAt(), token.createdAt()
    ));
  }

  @Override
  public Optional<ResetTokenRecord> findValidByTokenHash(String tokenHash, Instant now) {
    return jpa.findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(tokenHash, now)
        .map(e -> new ResetTokenRecord(
            e.getId(), e.getUserId(), e.getTokenHash(),
            e.getExpiresAt(), e.getUsedAt(), e.getCreatedAt()
        ));
  }

  @Override
  public void markUsed(UUID tokenId, Instant now) {
    var ent = jpa.findById(tokenId).orElseThrow(() -> new IllegalArgumentException("RESET_TOKEN_NOT_FOUND"));
    ent.setUsedAt(now);
    jpa.save(ent);
  }
}
